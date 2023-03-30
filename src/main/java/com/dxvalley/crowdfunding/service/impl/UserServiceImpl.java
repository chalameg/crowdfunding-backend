package com.dxvalley.crowdfunding.service.impl;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.ChangePassword;
import com.dxvalley.crowdfunding.dto.ResetPassword;
import com.dxvalley.crowdfunding.dto.UserDTO;
import com.dxvalley.crowdfunding.dto.mapper.UserDTOMapper;
import com.dxvalley.crowdfunding.exception.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.model.ConfirmationToken;
import com.dxvalley.crowdfunding.model.Users;
import com.dxvalley.crowdfunding.notification.EmailService;
import com.dxvalley.crowdfunding.notification.SmsService;
import com.dxvalley.crowdfunding.repository.ConfirmationTokenRepository;
import com.dxvalley.crowdfunding.repository.RoleRepository;
import com.dxvalley.crowdfunding.repository.UserRepository;
import com.dxvalley.crowdfunding.service.ConfirmationTokenService;
import com.dxvalley.crowdfunding.service.FileUploadService;
import com.dxvalley.crowdfunding.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Autowired
    private UserDTOMapper userDTOMapper;
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public List<UserDTO> getUsers() {
        try {
            List<Users> users = userRepository.findAll();
            if (hasSysAdminRole()) {
                List<UserDTO> result = users.stream().map(userDTOMapper).collect(Collectors.toList());
                if (result.isEmpty())
                    throw new ResourceNotFoundException("Currently, There is no User");

                logger.info("SuperAdmin Retrieved {} users", result.size());
                return result;
            } else {
                List<UserDTO> result = users.stream()
                        .filter(user -> user.getRoles().stream()
                                .noneMatch(role -> role.getRoleName().equals("SuperAdmin")))
                        .map(userDTOMapper)
                        .collect(Collectors.toList());

                if (result.isEmpty())
                    throw new ResourceNotFoundException("Currently, There is no User");

                logger.info("Retrieved {} users", result.size());
                return result;
            }
        } catch (DataAccessException ex) {
            logger.error("Error retrieving Users: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving Users", ex);
        }
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return new UserDTOMapper().apply(utilGetUserByUserId(userId));
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return new UserDTOMapper().apply(utilGetUserByUsername(username));
    }

    @Override
    public UserDTO register(Users tempUser) {
        try {
            if (userRepository.findByUsername(tempUser.getUsername()).isPresent())
                throw new ResourceAlreadyExistsException("There is already a user with this username.");

            LocalDateTime now = LocalDateTime.now();
            tempUser.setRoles(Collections.singletonList(roleRepo.findByRoleName("User")));
            tempUser.setPassword(passwordEncoder.encode(tempUser.getPassword()));
            tempUser.setCreatedAt(now.format(dateTimeFormatter));
            tempUser.setEditedAt(now.format(dateTimeFormatter));
            tempUser.setIsEnabled(false);

            if (emailService.isValidEmail(tempUser.getUsername())) {
                String token = UUID.randomUUID().toString();
                String link = "http://localhost:3000/verify/" + token;
                emailService.send(
                        tempUser.getUsername(),
                        emailService.emailBuilderForUserConfirmation(tempUser.getFullName(), link),
                        "Confirm your email");

                Users user = userRepository.save(tempUser);
                confirmationTokenService.saveConfirmationToken(user, token, 30);

                logger.info("New User is registered with email.");
                return new UserDTOMapper().apply(user);

            } else if (smsService.isValidPhoneNumber(tempUser.getUsername())) {
                String code = getRandomNumberString();
                smsService.sendOtp(tempUser.getUsername(), code);
                Users user = userRepository.save(tempUser);
                confirmationTokenService.saveConfirmationToken(user, code, 3);

                logger.info("New User is registered with phone number.");
                return new UserDTOMapper().apply(user);
            }
            throw new IllegalArgumentException("Username is neither a valid email nor a valid phone number.");
        } catch (DataAccessException ex) {
            logger.error("Error Registering new User: {}", ex.getMessage());
            throw new RuntimeException("Error Registering new User", ex);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmToken(String token) {
        try {
            ConfirmationToken confirmationToken = confirmationTokenService.getToken(token);
            LocalDateTime expiredAt = LocalDateTime.
                    parse(confirmationToken.getExpiresAt(), dateTimeFormatter);

            if (expiredAt.isBefore(LocalDateTime.now())) {
                confirmationTokenRepository.delete(confirmationToken);
                return false;
            }

            Users user = utilGetUserByUsername(confirmationToken.getUser().getUsername());
            user.setIsEnabled(true);
            user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
            userRepository.save(user);

            confirmationTokenRepository.delete(confirmationToken);
            logger.info("Token confirmed");
            return true;
        } catch (DataAccessException ex) {
            logger.error("Error Confirming token: {}", ex.getMessage());
            throw new RuntimeException("Error Confirming token", ex);
        }
    }

    @Override
    public UserDTO editUser(Long userId, UserDTO userDTO) {
        try {
            Users user = userRepository.findById(userId).
                    orElseThrow(() -> new ResourceNotFoundException("There is no user with this ID."));
            user.setFullName(userDTO.getFullName() != null ? userDTO.getFullName() : user.getFullName());
            user.setBiography(userDTO.getBiography() != null ? userDTO.getBiography() : user.getBiography());
            user.setWebsite(userDTO.getWebsite() != null ? userDTO.getWebsite() : user.getWebsite());
            user.setAddress(userDTO.getAddress() != null ? userDTO.getAddress() : user.getAddress());
            userDTO.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

            logger.info("User Edited");
            return new UserDTOMapper().apply(userRepository.save(user));
        } catch (DataAccessException ex) {
            logger.error("Error Editing user: {}", ex.getMessage());
            throw new RuntimeException("Error Editing user", ex);
        }
    }

    @Override
    public UserDTO uploadUserAvatar(String userName, MultipartFile userAvatar) {
        try {
            String avatarUrl = fileUploadService.uploadFile(userAvatar);
            Users user = utilGetUserByUsername(userName);
            user.setAvatarUrl(avatarUrl);
            user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
            Users result = userRepository.save(user);

            logger.info("User uploaded Avatar");
            return new UserDTOMapper().apply(result);
        } catch (DataAccessException ex) {
            logger.error("Error uploading User Avatar: {}", ex.getMessage());
            throw new RuntimeException("Error uploading User Avatar", ex);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse changePassword(String username, ChangePassword temp) {
        try {
            Users user = utilGetUserByUsername(username);
            Boolean isPasswordMatch = passwordEncoder.matches(temp.getOldPassword(), user.getPassword());
            if (!isPasswordMatch)
                return new ApiResponse("error", "Incorrect old Password!");

            user.setPassword(passwordEncoder.encode(temp.getNewPassword()));
            user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
            userRepository.save(user);

            logger.info("User changed password");
            return new ApiResponse("success", "Password Changed Successfully!");
        } catch (DataAccessException ex) {
            logger.error("Error changing Password: {}", ex.getMessage());
            throw new RuntimeException("Error changing Password", ex);
        }
    }

    @Override
    public ApiResponse forgotPassword(String username) {
        try {
            Users user = utilGetUserByUsername(username);
            if (emailService.isValidEmail(username)) {
                String token = UUID.randomUUID().toString();
                String link = "http://localhost:3000/resetPassword/" + token;

                emailService.send(
                        user.getUsername(),
                        emailService.emailBuilderForPasswordReset(user.getFullName(), link),
                        "Reset your password");

                confirmationTokenService.saveConfirmationToken(user, token, 30);

                logger.info("User asked of password reset with email");
                return new ApiResponse("success", "Please check your email");
            } else {
                String code = getRandomNumberString();
                smsService.sendOtp(username, code);
                confirmationTokenService.saveConfirmationToken(user, code, 3);

                logger.info("User asked for password reset with phone number");
                return new ApiResponse("success", "Please check your phone");
            }
        } catch (DataAccessException ex) {
            logger.error("Error in forgotPassword method: {}", ex.getMessage());
            throw new RuntimeException("Error in forgotPassword method", ex);
        }
    }

    @Override
    public ApiResponse resetPassword(ResetPassword resetPassword) {
        try {
            ConfirmationToken confirmationToken = confirmationTokenService.getToken(resetPassword.getToken());
            LocalDateTime expiredAt = LocalDateTime.parse(confirmationToken.getExpiresAt(), dateTimeFormatter);
            if (expiredAt.isBefore(LocalDateTime.now())) {
                return new ApiResponse(
                        "bad request",
                        "This token has already expired. Please reset again.");
            }

            var username = confirmationToken.getUser().getUsername();
            var user = utilGetUserByUsername(username);

            user.setPassword(passwordEncoder.encode(resetPassword.getPassword()));
            user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
            userRepository.save(user);
            confirmationTokenRepository.delete(confirmationToken);
            ApiResponse response = new ApiResponse(
                    "success",
                    "Hooray! Your password has been successfully reset.");
            return response;
        } catch (DataAccessException ex) {
            logger.error("Error resetting password: {}", ex.getMessage());
            throw new RuntimeException("Error resetting password.", ex);
        }
    }

    @Override
    public void delete(String username) {
        try {
            Users user = utilGetUserByUsername(username);
            userRepository.delete(user);
            logger.info("User with username {} Deleted", username);
        } catch (DataAccessException ex) {
            logger.error("Error Deleting User : {}", ex.getMessage());
            throw new RuntimeException("Error Deleting User", ex);
        }
    }

    //    Utility method for this class
    private boolean hasSysAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("SuperAdmin"));
    }

    private String getRandomNumberString() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    @Override
    public Users utilGetUserByUserId(Long userId) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("There is no user with this Id"));

            logger.info("Retrieving User by ID");
            return user;
        } catch (DataAccessException ex) {
            logger.error("Error retrieving User by Id: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving User ID", ex);
        }
    }

    @Override
    public Users utilGetUserByUsername(String username) {
        try {
            Users user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("There is no user with this username."));
            logger.info("Retrieving User by username");
            return user;
        } catch (DataAccessException ex) {
            logger.error("Error retrieving User by username: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving User username", ex);
        }
    }

}
