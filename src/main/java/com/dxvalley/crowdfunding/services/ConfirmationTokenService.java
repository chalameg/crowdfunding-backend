package com.dxvalley.crowdfunding.services;

import com.dxvalley.crowdfunding.models.ConfirmationToken;
import com.dxvalley.crowdfunding.models.Users;
import org.springframework.stereotype.Service;

@Service
public interface ConfirmationTokenService {
    ConfirmationToken saveConfirmationToken(Users user, String token, int expirationTimeInMinutes);

    ConfirmationToken getToken(String token);
}
