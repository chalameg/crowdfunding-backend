package com.dxvalley.crowdfunding.service.impl;

import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.model.ConfirmationToken;
import com.dxvalley.crowdfunding.model.Users;
import com.dxvalley.crowdfunding.notification.EmailService;
import com.dxvalley.crowdfunding.notification.SmsService;
import com.dxvalley.crowdfunding.repository.ConfirmationTokenRepository;
import com.dxvalley.crowdfunding.repository.UserRepository;
import com.dxvalley.crowdfunding.service.ConfirmationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SmsService smsService;
    @Autowired
    private DateTimeFormatter dateTimeFormatter;
    private final Logger logger = LoggerFactory.getLogger(ConfirmationTokenServiceImpl.class);

    @Override
    public ConfirmationToken saveConfirmationToken(Users user, String token, int expirationTimeInMinutes) {
        try {
            ConfirmationToken confirmationToken = new ConfirmationToken();
            confirmationToken.setUser(user);
            confirmationToken.setToken(token);
            confirmationToken.setCreatedAt(LocalDateTime.now().format(dateTimeFormatter));
            confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(expirationTimeInMinutes).format(dateTimeFormatter));
            return confirmationTokenRepository.save(confirmationToken);
        } catch (RuntimeException ex) {
            logger.error("Error saving confirmation token : {}", ex.getMessage());
            throw new RuntimeException("Error saving confirmation token", ex);
        }
    }

    @Override
    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Token"));
    }

    @Override
    public void sendConfirmationToken(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with this username."));

        if (emailService.isValidEmail(username)) {
            String token = UUID.randomUUID().toString();
            String link = "http://localhost:3000/verify/" + token;

            emailService.send(
                    username,
                    emailService.emailBuilderForUserConfirmation(user.getFullName(), link),
                    "Confirm your email");

            saveConfirmationToken(user, token, 30);
            logger.info("Confirmation email is sent to the user.");
        } else {
            String code = String.format("%06d", new Random().nextInt(999999));
            smsService.sendOtp(user.getUsername(), code);
            saveConfirmationToken(user, code, 3);
            logger.info("Confirmation Token is sent to the user.");
        }
    }
}
