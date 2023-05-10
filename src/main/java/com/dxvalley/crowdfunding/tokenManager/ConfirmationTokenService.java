package com.dxvalley.crowdfunding.tokenManager;

import com.dxvalley.crowdfunding.user.userRole.Users;
import org.springframework.stereotype.Service;

@Service
public interface ConfirmationTokenService {
    ConfirmationToken saveConfirmationToken(Users user, String token, int expirationTimeInMinutes);
    ConfirmationToken getToken(String token);
    void sendConfirmationToken(String username);
}
