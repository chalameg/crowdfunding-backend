package com.dxvalley.crowdfunding.services.impl;

import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.services.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.ConfirmationToken;
import com.dxvalley.crowdfunding.repositories.ConfirmationTokenRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ConfirmationToken saveConfirmationToken(Users user, String token, int expirationTimeInMinutes) {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setUser(user);
        confirmationToken.setToken(token);
        confirmationToken.setCreatedAt(LocalDateTime.now().format(dateTimeFormatter));
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(expirationTimeInMinutes).format(dateTimeFormatter));
        return confirmationTokenRepository.save(confirmationToken);
    }

    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository.findByToken(token).orElseThrow(
                () -> new ResourceNotFoundException("Invalid Token")
        );
    }

}
