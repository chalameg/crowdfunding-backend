package com.dxvalley.crowdfunding.userManager.user;

import com.dxvalley.crowdfunding.exception.customException.BadRequestException;
import com.dxvalley.crowdfunding.fileUploadManager.FileUploadService;
import com.dxvalley.crowdfunding.messageManager.email.EmailBuilder;
import com.dxvalley.crowdfunding.messageManager.email.EmailService;
import com.dxvalley.crowdfunding.messageManager.sms.SmsService;
import com.dxvalley.crowdfunding.tokenManager.ConfirmationToken;
import com.dxvalley.crowdfunding.tokenManager.ConfirmationTokenRepository;
import com.dxvalley.crowdfunding.tokenManager.ConfirmationTokenService;
import com.dxvalley.crowdfunding.userManager.userDTO.*;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final SmsService smsService;
    private final FileUploadService fileUploadService;
    private final DateTimeFormatter dateTimeFormatter;
    private final UserUtils userUtils;

    public UserResponse getUserById(Long userId) {
        return UserMapper.toUserResponse(this.userUtils.utilGetUserByUserId(userId));
    }

    public UserResponse getUserByUsername(String username) {
        return UserMapper.toUserResponse(this.userUtils.utilGetUserByUsername(username));
    }


    @Override
    public UserResponse register(UserRegistrationReq userRegistrationReq) {
        String username = userRegistrationReq.getUsername();
        this.userUtils.validateUsername(username);
        this.userUtils.validateUsernameAvailability(username);

        Users user = this.userUtils.createUser(username, userRegistrationReq);
        Users savedUser = this.userUtils.saveUser(user);
        this.confirmationTokenService.sendConfirmationToken(savedUser);

        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse editUser(Long userId, UserUpdateReq updateReq) {
        Users user = this.userUtils.utilGetUserByUserId(userId);
        if (updateReq.getFullName() != null) {
            user.setFullName(updateReq.getFullName());
        }

        if (updateReq.getBiography() != null) {
            user.setBiography(updateReq.getBiography());
        }

        if (updateReq.getWebsite() != null) {
            user.setWebsite(updateReq.getWebsite());
        }

        if (updateReq.getAddress() != null) {
            user.setAddress(updateReq.getAddress());
        }

        if (updateReq.getEmail() != null) {
            user.setEmail(updateReq.getEmail());
        }

        user.setEditedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        Users updatedUser = this.userUtils.saveUser(user);
        return UserMapper.toUserResponse(updatedUser);
    }

    @Override
    public UserResponse uploadUserAvatar(String userName, MultipartFile userAvatar) {
        String avatarUrl = this.fileUploadService.uploadFile(userAvatar);
        Users user = this.userUtils.utilGetUserByUsername(userName);
        user.setAvatarUrl(avatarUrl);
        user.setEditedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        Users result = this.userUtils.saveUser(user);

        return UserMapper.toUserResponse(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ApiResponse> changePassword(String username, ChangePassword temp) {
        Users user = this.userUtils.utilGetUserByUsername(username);

        this.validateOldPassword(user, temp.getOldPassword());

        user.setPassword(this.passwordEncoder.encode(temp.getNewPassword()));
        user.setEditedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        this.userUtils.saveUser(user);

        return ApiResponse.success("Password Changed Successfully!");
    }

    private void validateOldPassword(Users user, String oldPassword) {
        boolean isPasswordMatch = this.passwordEncoder.matches(oldPassword, user.getPassword());
        if (!isPasswordMatch) {
            throw new BadRequestException("Incorrect old Password!");
        }
    }


    @Override
    public ResponseEntity<ApiResponse> forgotPassword(String username) {
        Users user = this.userUtils.utilGetUserByUsername(username);
        String code;
        if (this.emailService.isValidEmail(username)) {
            code = UUID.randomUUID().toString();
            String link = "http://10.100.51.60/resetPassword/" + code;
            this.emailService.send(user.getUsername(), EmailBuilder.emailBuilderForPasswordReset(user.getFullName(), link), "Reset your password");
            this.confirmationTokenService.saveConfirmationToken(user, code, 30);
            return ApiResponse.success("Please check your email");
        } else {
            code = String.format("%06d", (new Random()).nextInt(999999));
            this.smsService.sendOtp(username, code);
            this.confirmationTokenService.saveConfirmationToken(user, code, 3);
            return ApiResponse.success("Please check your phone");
        }
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponse> resetPassword(ResetPassword resetPassword) {
        ConfirmationToken confirmationToken = confirmationTokenService.checkTokenExpiration(resetPassword.getToken());
        String username = confirmationToken.getUser().getUsername();
        Users user = userUtils.utilGetUserByUsername(username);

        user.setPassword(passwordEncoder.encode(resetPassword.getPassword()));
        user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
        userUtils.saveUser(user);
        confirmationTokenRepository.delete(confirmationToken);

        return ApiResponse.success("Hooray! Your password has been successfully reset.");
    }

    @Override
    public ResponseEntity<ApiResponse> delete(String username) {
        userUtils.delete(username);

        return ApiResponse.success("User deleted successfully.");
    }

}
