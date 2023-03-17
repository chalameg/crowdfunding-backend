package com.dxvalley.crowdfunding.service.impl;

import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.model.ConfirmationToken;
import com.dxvalley.crowdfunding.model.Users;
import com.dxvalley.crowdfunding.repository.ConfirmationTokenRepository;
import com.dxvalley.crowdfunding.service.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ConfirmationToken saveConfirmationToken(Users user, String token, int expirationTimeInMinutes) {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setUser(user);
        confirmationToken.setToken(token);
        confirmationToken.setCreatedAt(LocalDateTime.now().format(dateTimeFormatter));
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(expirationTimeInMinutes).format(dateTimeFormatter));
        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository.findByToken(token).orElseThrow(
                () -> new ResourceNotFoundException("Invalid Token")
        );
    }

}
