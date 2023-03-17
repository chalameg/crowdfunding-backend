package com.dxvalley.crowdfunding.service;

import com.dxvalley.crowdfunding.model.ConfirmationToken;
import com.dxvalley.crowdfunding.model.Users;
import org.springframework.stereotype.Service;

@Service
public interface ConfirmationTokenService {
    ConfirmationToken saveConfirmationToken(Users user, String token, int expirationTimeInMinutes);

    ConfirmationToken getToken(String token);
}
