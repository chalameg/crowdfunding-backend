package com.dxvalley.crowdfunding.user;

import com.dxvalley.crowdfunding.exception.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.fileUpload.FileUploadService;
import com.dxvalley.crowdfunding.messageManager.email.EmailBuilder;
import com.dxvalley.crowdfunding.messageManager.email.EmailService;
import com.dxvalley.crowdfunding.messageManager.sms.SmsService;
import com.dxvalley.crowdfunding.tokenManager.ConfirmationToken;
import com.dxvalley.crowdfunding.tokenManager.ConfirmationTokenRepository;
import com.dxvalley.crowdfunding.tokenManager.ConfirmationTokenService;
import com.dxvalley.crowdfunding.user.dto.ChangePassword;
import com.dxvalley.crowdfunding.user.dto.ResetPassword;
import com.dxvalley.crowdfunding.user.dto.UserDTO;
import com.dxvalley.crowdfunding.user.dto.UserDTOMapper;
import com.dxvalley.crowdfunding.user.role.RoleRepository;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final EmailBuilder emailBuilder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final SmsService smsService;
    private final FileUploadService fileUploadService;
    private final DateTimeFormatter dateTimeFormatter;

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
            tempUser.setUserStatus(UserStatus.ACTIVE);
            tempUser.setIsEnabled(false);
            tempUser.setContributions((short) 0);
            tempUser.setTotalAmountSpent(0.0);
            tempUser.setTotalCampaigns((short) 0);

            if (emailService.isValidEmail(tempUser.getUsername())) {
                String token = UUID.randomUUID().toString();
                String link = "http://10.1.177.121/verify/" + token;
                emailService.send(
                        tempUser.getUsername(),
                        emailBuilder.emailBuilderForUserConfirmation(tempUser.getFullName(), link),
                        "Confirm your email");

                Users user = userRepository.save(tempUser);
                confirmationTokenService.saveConfirmationToken(user, token, 30);

                log.info("New User is registered with email.");
                return new UserDTOMapper().apply(user);

            } else if (smsService.isValidPhoneNumber(tempUser.getUsername())) {
                String code = getRandomNumberString();
                smsService.sendOtp(tempUser.getUsername(), code);
                Users user = userRepository.save(tempUser);
                confirmationTokenService.saveConfirmationToken(user, code, 3);

                log.info("New User is registered with phone number.");
                return new UserDTOMapper().apply(user);
            }
            throw new IllegalArgumentException("Username is neither a valid email nor a valid phone number.");
        } catch (DataAccessException ex) {
            log.error("Error Registering new User: {}", ex.getMessage());
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
            log.info("Token confirmed");
            return true;
        } catch (DataAccessException ex) {
            log.error("Error Confirming token: {}", ex.getMessage());
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

            log.info("User Edited");
            return new UserDTOMapper().apply(userRepository.save(user));
        } catch (DataAccessException ex) {
            log.error("Error Editing user: {}", ex.getMessage());
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

            log.info("User uploaded Avatar");
            return new UserDTOMapper().apply(result);
        } catch (DataAccessException ex) {
            log.error("Error uploading User Avatar: {}", ex.getMessage());
            throw new RuntimeException("Error uploading User Avatar", ex);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity changePassword(String username, ChangePassword temp) {
        try {
            Users user = utilGetUserByUsername(username);
            Boolean isPasswordMatch = passwordEncoder.matches(temp.getOldPassword(), user.getPassword());
            if (!isPasswordMatch)
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Incorrect old Password!");

            user.setPassword(passwordEncoder.encode(temp.getNewPassword()));
            user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
            userRepository.save(user);

            log.info("User changed password");
            return ApiResponse.success("Password Changed Successfully!");
        } catch (DataAccessException ex) {
            log.error("Error changing Password: {}", ex.getMessage());
            throw new RuntimeException("Error changing Password", ex);
        }
    }

    @Override
    public ResponseEntity forgotPassword(String username) {
        try {
            Users user = utilGetUserByUsername(username);
            if (emailService.isValidEmail(username)) {
                String token = UUID.randomUUID().toString();
                String link = "http://10.1.177.121/resetPassword/" + token;

                emailService.send(
                        user.getUsername(),
                        emailBuilder.emailBuilderForPasswordReset(user.getFullName(), link),
                        "Reset your password");

                confirmationTokenService.saveConfirmationToken(user, token, 30);

                log.info("User asked of password reset with email");
                return ApiResponse.success("Please check your email");
            } else {
                String code = getRandomNumberString();
                smsService.sendOtp(username, code);
                confirmationTokenService.saveConfirmationToken(user, code, 3);

                log.info("User asked for password reset with phone number");
                return ApiResponse.success("Please check your phone");
            }
        } catch (DataAccessException ex) {
            log.error("Error in forgotPassword method: {}", ex.getMessage());
            throw new RuntimeException("Error in forgotPassword method", ex);
        }
    }

    @Override
    public ResponseEntity resetPassword(ResetPassword resetPassword) {
        try {
            ConfirmationToken confirmationToken = confirmationTokenService.getToken(resetPassword.getToken());
            LocalDateTime expiredAt = LocalDateTime.parse(confirmationToken.getExpiresAt(), dateTimeFormatter);
            if (expiredAt.isBefore(LocalDateTime.now())) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST,
                        "This token has already expired. Please reset again.");
            }

            var username = confirmationToken.getUser().getUsername();
            var user = utilGetUserByUsername(username);

            user.setPassword(passwordEncoder.encode(resetPassword.getPassword()));
            user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
            userRepository.save(user);
            confirmationTokenRepository.delete(confirmationToken);
            return ApiResponse.success("Hooray! Your password has been successfully reset.");
        } catch (DataAccessException ex) {
            log.error("Error resetting password: {}", ex.getMessage());
            throw new RuntimeException("Error resetting password.", ex);
        }
    }

    @Override
    public void delete(String username) {
        try {
            Users user = utilGetUserByUsername(username);
            userRepository.delete(user);
            log.info("User with username {} Deleted", username);
        } catch (DataAccessException ex) {
            log.error("Error Deleting User : {}", ex.getMessage());
            throw new RuntimeException("Error Deleting User", ex);
        }
    }

    @Override
    public Users saveUser(Users user) {
        try {
            var savedUser = userRepository.save(user);
            log.info("User with username {} Updated", savedUser.getUsername());
            return savedUser;
        } catch (DataAccessException ex) {
            log.error("Error Saving User : {}", ex.getMessage());
            throw new RuntimeException("Error Saving User", ex);
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

            log.info("Retrieving User by ID");
            return user;
        } catch (DataAccessException ex) {
            log.error("Error retrieving User by Id: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving User ID", ex);
        }
    }

    @Override
    public Users utilGetUserByUsername(String username) {
        try {
            Users user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("There is no user with this username."));
            log.info("Retrieving User by username");
            return user;
        } catch (DataAccessException ex) {
            log.error("Error retrieving User by username: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving User username", ex);
        }
    }

}
