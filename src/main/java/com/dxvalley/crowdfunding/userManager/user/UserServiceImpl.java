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

    @Override
    public UserResponse getUserById(Long userId) {
        return UserMapper.toUserResponse(userUtils.utilGetUserByUserId(userId));
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        return UserMapper.toUserResponse(userUtils.utilGetUserByUsername(username));
    }

    @Override
    public UserResponse register(UserRegistrationReq userRegistrationReq) {
        String username = userRegistrationReq.getUsername();
        userUtils.validateUsername(username);
        userUtils.validateUsernameAvailability(username);

        Users user = userUtils.createUser(userRegistrationReq);
        Users savedUser = userUtils.saveUser(user);
        confirmationTokenService.sendConfirmationToken(savedUser);

        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse editUser(Long userId, UserUpdateReq updateReq) {
        Users user = userUtils.utilGetUserByUserId(userId);

        if (updateReq.getFullName() != null)
            user.setFullName(updateReq.getFullName());

        if (updateReq.getBiography() != null)
            user.setBiography(updateReq.getBiography());

        if (updateReq.getWebsite() != null)
            user.setWebsite(updateReq.getWebsite());

        if (updateReq.getAddress() != null)
            user.setAddress(updateReq.getAddress());

        user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

        Users updatedUser = userUtils.saveUser(user);
        return UserMapper.toUserResponse(updatedUser);
    }

    @Override
    public UserResponse uploadUserAvatar(String userName, MultipartFile userAvatar) {
        String avatarUrl = fileUploadService.uploadFile(userAvatar);
        Users user = userUtils.utilGetUserByUsername(userName);
        user.setAvatarUrl(avatarUrl);
        user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
        Users result = userUtils.saveUser(user);

        return UserMapper.toUserResponse(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ApiResponse> changePassword(String username, ChangePassword temp) {
        Users user = userUtils.utilGetUserByUsername(username);

        validateOldPassword(user, temp.getOldPassword());

        user.setPassword(passwordEncoder.encode(temp.getNewPassword()));
        user.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
        userUtils.saveUser(user);

        return ApiResponse.success("Password Changed Successfully!");
    }

    private void validateOldPassword(Users user, String oldPassword) {
        boolean isPasswordMatch = passwordEncoder.matches(oldPassword, user.getPassword());
        if (!isPasswordMatch)
            throw new BadRequestException("Incorrect old Password!");
    }


    @Override
    public ResponseEntity<ApiResponse> forgotPassword(String username) {
        Users user = userUtils.utilGetUserByUsername(username);
        if (emailService.isValidEmail(username)) {
            String token = UUID.randomUUID().toString();
            String link = "http://10.1.177.121/resetPassword/" + token;

            emailService.send(
                    user.getUsername(),
                    EmailBuilder.emailBuilderForPasswordReset(user.getFullName(), link),
                    "Reset your password");

            confirmationTokenService.saveConfirmationToken(user, token, 30);

            return ApiResponse.success("Please check your email");
        } else {
            String code = String.format("%06d", new Random().nextInt(999999));
            smsService.sendOtp(username, code);
            confirmationTokenService.saveConfirmationToken(user, code, 3);

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
