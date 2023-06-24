package com.dxvalley.crowdfunding.tokenManager;

import com.dxvalley.crowdfunding.userManager.user.Users;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ConfirmationTokenService {
    ConfirmationToken saveConfirmationToken(Users user, String token, int expirationTimeInMinutes);

    ConfirmationToken getToken(String token);

    void sendConfirmationToken(String contact);

    void sendConfirmationToken(Users users);

    ResponseEntity<ApiResponse> confirmToken(String token);

    ConfirmationToken checkTokenExpiration(String token);
}
