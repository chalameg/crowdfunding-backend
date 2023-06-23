package com.dxvalley.crowdfunding.tokenManager;

import com.dxvalley.crowdfunding.exception.customException.BadRequestException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.messageManager.email.EmailBuilder;
import com.dxvalley.crowdfunding.messageManager.email.EmailService;
import com.dxvalley.crowdfunding.messageManager.sms.SmsService;
import com.dxvalley.crowdfunding.userManager.user.UserUtils;
import com.dxvalley.crowdfunding.userManager.user.Users;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;
    private final EmailBuilder emailBuilder;
    private final UserUtils userUtils;
    private final SmsService smsService;
    private final DateTimeFormatter dateTimeFormatter;

    public ConfirmationToken getToken(String token) {
        return (ConfirmationToken)this.confirmationTokenRepository.findByToken(token).orElseThrow(() -> {
            return new ResourceNotFoundException("Invalid Token");
        });
    }

    public void sendConfirmationToken(String contact) {
        Users user = this.userUtils.utilGetUserByUsername(contact);
        if (this.emailService.isValidEmail(contact)) {
            this.sendEmailConfirmation(user);
        } else {
            this.sendOtpConfirmation(user);
        }

    }

    public void sendConfirmationToken(Users user) {
        String username = user.getUsername();
        if (this.emailService.isValidEmail(username)) {
            this.sendEmailConfirmation(user);
        } else {
            this.sendOtpConfirmation(user);
        }

    }

    private void sendEmailConfirmation(Users user) {
        String token = UUID.randomUUID().toString();
        String link = "http://10.100.51.60/verify/" + token;
        EmailService var10000 = this.emailService;
        String var10001 = user.getUsername();
        EmailBuilder var10002 = this.emailBuilder;
        var10000.send(var10001, EmailBuilder.emailBuilderForUserConfirmation(user.getFullName(), link), "Confirm your email");
        this.saveConfirmationToken(user, token, 30);
    }

    private void sendOtpConfirmation(Users user) {
        String token = String.format("%06d", (new Random()).nextInt(999999));
        this.smsService.sendOtp(user.getUsername(), token);
        this.saveConfirmationToken(user, token, 3);
    }

    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<ApiResponse> confirmToken(String token) {
        ConfirmationToken confirmationToken = this.checkTokenExpiration(token);
        String username = confirmationToken.getUser().getUsername();
        Users user = this.userUtils.utilGetUserByUsername(username);
        user.setVerified(true);
        user.setEditedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        this.userUtils.saveUser(user);
        this.confirmationTokenRepository.delete(confirmationToken);
        return ApiResponse.success("Confirmed successful.");
    }

    public ConfirmationToken checkTokenExpiration(String token) {
        ConfirmationToken confirmationToken = (ConfirmationToken)this.confirmationTokenRepository.findByToken(token).orElseThrow(() -> {
            return new BadRequestException("Invalid Token");
        });
        LocalDateTime expiredAt = LocalDateTime.parse(confirmationToken.getExpiresAt(), this.dateTimeFormatter);
        if (expiredAt.isBefore(LocalDateTime.now())) {
            this.confirmationTokenRepository.delete(confirmationToken);
            throw new BadRequestException("The token has expired.");
        } else {
            return confirmationToken;
        }
    }

    public ConfirmationToken saveConfirmationToken(Users user, String token, int expirationTimeInMinutes) {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setUser(user);
        confirmationToken.setToken(token);
        confirmationToken.setCreatedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes((long)expirationTimeInMinutes).format(this.dateTimeFormatter));
        return (ConfirmationToken)this.confirmationTokenRepository.save(confirmationToken);
    }
}
