package com.dxvalley.crowdfunding.services.impl;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.ResetPassword;
import com.dxvalley.crowdfunding.dto.ChangePassword;
import com.dxvalley.crowdfunding.notification.EmailService;
import com.dxvalley.crowdfunding.exceptions.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.models.ConfirmationToken;
import com.dxvalley.crowdfunding.notification.SmsService;
import com.dxvalley.crowdfunding.repositories.ConfirmationTokenRepository;
import com.dxvalley.crowdfunding.repositories.RoleRepository;
import com.dxvalley.crowdfunding.repositories.UserRepository;

import com.dxvalley.crowdfunding.services.ConfirmationTokenService;
import com.dxvalley.crowdfunding.services.FileUploadService;
import com.dxvalley.crowdfunding.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private ConfirmationTokenService confirmationTokenService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private SmsService smsService;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private DateTimeFormatter dateTimeFormatter;

    private boolean isSysAdmin() {
        AtomicBoolean hasSysAdmin = new AtomicBoolean(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auth.getAuthorities().forEach(grantedAuthority -> {
            if (grantedAuthority.getAuthority().equals("sysAdmin")) {
                hasSysAdmin.set(true);
            }
        });
        return hasSysAdmin.get();
    }

    public List<Users> getUsers() {
        if (isSysAdmin()) {
            return userRepository.findAll();
        }
        var users = this.userRepository.findAll();
        users.removeIf(user -> {
            var containsAdmin = false;
            for (var role : user.getRoles()) {
                containsAdmin = containsAdmin || role.getRoleName().equals("admin");
            }
            return containsAdmin;
        });
        return users;
    }

    public Users getUserById(Long userId) {
        Users user = userRepository.findByUserId(userId).orElseThrow(
                () -> new ResourceNotFoundException("There is no user with this Id")
        );
        return user;
    }

    public Users getUserByUsername(String username) {
        var user = userRepository.findUsersByUsernameAndIsEnabled(username, true).orElseThrow(
                () -> new ResourceNotFoundException("There is no user with this username")
        );
        return user;
    }

    public ResponseEntity<?> register(Users tempUser) {
        var existingUser = userRepository.findUsersByUsernameAndIsEnabled(tempUser.getUsername(), true);
        if (existingUser.isPresent())
            throw new ResourceAlreadyExistsException("There is already a user with this username");

        // Check if there is a disabled user with the same username and delete it
        userRepository.findUsersByUsernameAndIsEnabled(tempUser.getUsername(), false).ifPresent(
                disabledUser -> userRepository.delete(disabledUser)
        );

        LocalDateTime now = LocalDateTime.now();
        tempUser.setRoles(Collections.singletonList(roleRepo.findByRoleName("user")));
        tempUser.setPassword(passwordEncoder.encode(tempUser.getPassword()));
        tempUser.setCreatedAt(now.format(dateTimeFormatter));
        tempUser.setEditedAt(now.format(dateTimeFormatter));
        tempUser.setIsEnabled(false);

        if (emailService.isValidEmail(tempUser.getUsername())) {
            String token = UUID.randomUUID().toString();
            String link = "http://localhost:3000/verify/" + token;
            boolean isSend = emailService.send(
                    tempUser.getUsername(),
                    emailService.emailBuilderForUserConfirmation(tempUser.getFullName(), link),
                    "Confirm your email");
            if (isSend) {
                Users user = userRepository.save(tempUser);
                confirmationTokenService.saveConfirmationToken(user, token, 30);
                return new ResponseEntity<>(user, HttpStatus.CREATED);
            }
            ApiResponse response = new ApiResponse(
                    "error",
                    "Cannot sent email due to Internal Server Error. try again later");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (smsService.isValidPhoneNumber(tempUser.getUsername())) {
            String code = getRandomNumberString();
            var result = smsService.sendOtp(tempUser.getUsername(), code);
            if (result.getStatus().equals("success")) {
                Users user = userRepository.save(tempUser);
                confirmationTokenService.saveConfirmationToken(user, code, 3);
                return new ResponseEntity<>(user, HttpStatus.CREATED);
            }
            ApiResponse response = new ApiResponse(
                    "error",
                    "Cannot sent sms due to Internal Server Error. try again later");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ApiResponse response = new ApiResponse(
                "error",
                "username is neither a valid email nor a valid phone number.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public boolean confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token);
        LocalDateTime expiredAt = LocalDateTime.parse(confirmationToken.getExpiresAt(), dateTimeFormatter);
        if (expiredAt.isBefore(LocalDateTime.now())) {
            confirmationTokenRepository.delete(confirmationToken);
            return false;
        }

        var username = confirmationToken.getUser().getUsername();
        var user = getUserByUsername(username);
        user.setIsEnabled(true);
        user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
        userRepository.save(user);
        confirmationTokenRepository.delete(confirmationToken);
        return true;
    }

    public Users editUser(Long userId, Users tempUser) {
        Users user = getUserById(userId);

        user.setFullName(tempUser.getFullName() != null ? tempUser.getFullName() : user.getFullName());
        user.setBiography(tempUser.getBiography() != null ? tempUser.getBiography() : user.getBiography());
        user.setWebsite(tempUser.getWebsite() != null ? tempUser.getWebsite() : user.getWebsite());
        user.setAddress(tempUser.getAddress() != null ? tempUser.getAddress() : user.getAddress());
        tempUser.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

        Users result = userRepository.save(user);
        result.setPassword(null);
        return result;
    }

    public ResponseEntity<?> uploadUserAvatar(String userName, MultipartFile userAvatar) {
        var user = getUserByUsername(userName);
        String avatarUrl;
        try {
            avatarUrl = fileUploadService.uploadFile(userAvatar);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    "bad request",
                    "Bad file size or format!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        user.setAvatarUrl(avatarUrl);
        user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
        var result = userRepository.save(user);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public ApiResponse changePassword(String username, ChangePassword temp) {
        var user = userRepository.findUsersByUsernameAndIsEnabled(username, true).orElseThrow(
                () -> new ResourceNotFoundException("There is no user with this username")
        );
        Boolean isMatch = passwordEncoder.matches(temp.getOldPassword(), user.getPassword());
        if (!isMatch) {
            return new ApiResponse("error", "Incorrect old Password!");
        }

        user.setPassword(passwordEncoder.encode(temp.getNewPassword()));
        user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
        userRepository.save(user);
        return new ApiResponse("success", "Password Changed Successfully!");
    }

    public ApiResponse forgotPassword(String username) {
        Users user = getUserByUsername(username);

        if (username.matches(".*[a-zA-Z]+.*")) {
            String token = UUID.randomUUID().toString();
            String link = "http://localhost:3000/resetPassword/" + token;

            Boolean isSend = null;
            try {
                isSend = emailService.send(
                        user.getUsername(),
                        emailService.emailBuilderForPasswordReset(user.getFullName(), link),
                        "Reset your password");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (isSend) {
                confirmationTokenService.saveConfirmationToken(user, token, 30);
                return new ApiResponse("success", "please check your email");
            }
            return new ApiResponse(
                    "error",
                    "Cannot sent email due to Internal Server Error. try again later");

        } else {
            String code = getRandomNumberString();
            var result = smsService.sendOtp(user.getUsername(), code);
            if (result.getStatus().equals("success")) {
                confirmationTokenService.saveConfirmationToken(user, code, 3);
                return result;
            }
            return result;
        }
    }

    public ApiResponse resetPassword(ResetPassword resetPassword) {

        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(resetPassword.getToken());

        LocalDateTime expiredAt = LocalDateTime.parse(confirmationToken.getExpiresAt(), dateTimeFormatter);
        if (expiredAt.isBefore(LocalDateTime.now())) {
            return new ApiResponse(
                    "bad request",
                    "This token has already expired. Please reset again.");
        }

        var username = confirmationToken.getUser().getUsername();
        var user = getUserByUsername(username);

        user.setPassword(passwordEncoder.encode(resetPassword.getPassword()));
        user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
        userRepository.save(user);
        confirmationTokenRepository.delete(confirmationToken);
        ApiResponse response = new ApiResponse(
                "success",
                "Hooray! Your password has been successfully reset.");
        return response;
    }

    public void delete(String username) {
        Users user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("There is no User with this username.")
        );
        userRepository.delete(user);
    }

    public String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

}
