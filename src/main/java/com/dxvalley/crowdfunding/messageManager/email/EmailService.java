package com.dxvalley.crowdfunding.messageManager.email;

import com.dxvalley.crowdfunding.utils.ApiResponse;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    boolean isValidEmail(String email);

    CompletableFuture<ApiResponse> send(String recipientEmail, String emailBody, String emailSubject);
}
