package com.dxvalley.crowdfunding.services.impl;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.ResetPassword;
import com.dxvalley.crowdfunding.dto.ChangePassword;
import com.dxvalley.crowdfunding.notification.EmailSender;
import com.dxvalley.crowdfunding.exceptions.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.Role;
import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.models.ConfirmationToken;
import com.dxvalley.crowdfunding.notification.OtpService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private ConfirmationTokenService confirmationTokenService;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    OtpService otpService;
    @Autowired
    FileUploadService fileUploadService;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
    public List<Users> getUsers(){
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
    public Users getUserById(Long userId){
         Users user = userRepository.findByUserId(userId).orElseThrow(
                () -> new ResourceNotFoundException("There is no user with this Id")
        );
        user.setPassword(null);
        return user;
    }
    public Users getUserByUsername(String username) {
        var user = userRepository.findUserByUsername(username, true).orElseThrow(
                () -> new ResourceNotFoundException("There is no user with this username")
        );
        user.setPassword(null);
        return user;
    }
    public ResponseEntity<?> register(Users tempUser) {

        userRepository.findUserByUsername(tempUser.getUsername(), true).ifPresent(
                user -> {throw new ResourceAlreadyExistsException(
                        "There is already a user with this username, known " + user.getFullName()); }
        );
        userRepository.findUserByUsername(tempUser.getUsername(),false).ifPresent(
                user -> userRepository.delete(user)
        );

        List<Role> roles = new ArrayList<Role>(1);
        roles.add(this.roleRepo.findByRoleName("user"));
        tempUser.setRoles(roles);
        tempUser.setPassword(passwordEncoder.encode(tempUser.getPassword()));
        tempUser.setIsEnabled(false);
        var user =  userRepository.save(tempUser);
        user.setPassword(null);

        //send email if username is email
        if (tempUser.getUsername().matches(".*[a-zA-Z]+.*")) {
            String token = UUID.randomUUID().toString();
            String link = "http://localhost:3000/verify/" + token;
            Boolean isSend = emailSender.send(
                    tempUser.getUsername(),
                    emailSender.buildEmail(tempUser.getFullName(), link),
                    "Confirm your email");
            if(isSend){
                LocalDateTime createdAt = LocalDateTime.now();
                LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

                ConfirmationToken confirmationToken = new ConfirmationToken(
                        token,
                        createdAt.format(dateTimeFormatter),
                        expiresAt.format(dateTimeFormatter),
                        user);
                confirmationTokenService.saveConfirmationToken(confirmationToken);
                return new ResponseEntity<>(user,HttpStatus.OK);
            }
            ApiResponse response = new ApiResponse(
                    "error",
                    "Cannot sent email due to Internal Server Error. try again later");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            // send message if username is phone number
            String code = getRandomNumberString();
            var result = otpService.sendOtp(tempUser.getUsername(),code);
            if(result.getStatus() =="success" ){
                LocalDateTime createdAt = LocalDateTime.now();
                LocalDateTime expiresAt = LocalDateTime.now().plusDays(5);

                ConfirmationToken token = new ConfirmationToken(
                        code,
                        createdAt.format(dateTimeFormatter),
                        expiresAt.format(dateTimeFormatter),
                        user
                );
                confirmationTokenService.saveConfirmationToken(token);
                return new ResponseEntity<>(user,HttpStatus.OK);
            }
            if(result.getStatus()=="error")
                return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
        }
    }
    public Boolean confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token);

        LocalDateTime expiredAt = LocalDateTime.parse(confirmationToken.getExpiresAt(), dateTimeFormatter);
        if (expiredAt.isBefore(LocalDateTime.now())){
            confirmationTokenRepository.delete(confirmationToken);
            return false;
        }

        var username = confirmationToken.getUser().getUsername();
        userRepository.enableUser(username);
        confirmationTokenRepository.delete(confirmationToken);
        return true;
    }
    public Users editUser(Long userId,  Users tempUser){
        Users user = getUserById(userId);

        user.setFullName(tempUser.getFullName() != null ? tempUser.getFullName() : user.getFullName());
        user.setBiography(tempUser.getBiography() != null ? tempUser.getBiography() : user.getBiography());
        user.setWebsite(tempUser.getWebsite() != null ? tempUser.getWebsite() : user.getWebsite());
        user.setAddress(tempUser.getAddress() != null ? tempUser.getAddress() : user.getAddress());

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
        var result = userRepository.save(user);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    public ApiResponse changePassword(String username, ChangePassword temp) {
        var user = userRepository.findUserByUsername(username, true).orElseThrow(
                () -> new ResourceNotFoundException("There is no user with this username")
        );

        Boolean isMatch =  passwordEncoder.matches(temp.getOldPassword(), user.getPassword());
        if (!isMatch) {
            return new ApiResponse("error", "Incorrect old Password!");
        }

        user.setPassword(passwordEncoder.encode(temp.getNewPassword()));
        userRepository.save(user);
        return new ApiResponse("success", "Password Changed Successfully!");
    }
    public ApiResponse forgotPassword(String username) {
        Users user = getUserByUsername(username);

        if (username.matches(".*[a-zA-Z]+.*")) {
            String token = UUID.randomUUID().toString();
            String link = "http://localhost:3000/resetPassword?token=" + token;

            Boolean isSend  = emailSender.send(
                    user.getUsername(),
                    emailSender.emailBuilderForPasswordReset(user.getFullName(), link),
                    "Reset your password");
            if(isSend){
                LocalDateTime createdAt = LocalDateTime.now();
                LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

                ConfirmationToken confirmationToken = new ConfirmationToken(
                        token,
                        createdAt.format(dateTimeFormatter),
                        expiresAt.format(dateTimeFormatter),
                        user);
                confirmationTokenService.saveConfirmationToken(confirmationToken);
                return new ApiResponse("success","please check your email");
            }
            return new ApiResponse(
                    "error",
                    "Cannot sent email due to Internal Server Error. try again later");

        }else {
            String code = getRandomNumberString();
            var result = otpService.sendOtp(user.getUsername(),code);

            if(result.getStatus() =="success" ){

                LocalDateTime createdAt = LocalDateTime.now();
                LocalDateTime expiresAt = LocalDateTime.now().plusDays(5);

                ConfirmationToken token = new ConfirmationToken(
                        code,
                        createdAt.format(dateTimeFormatter),
                        expiresAt.format(dateTimeFormatter),
                        user
                );
                confirmationTokenService.saveConfirmationToken(token);
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
