package com.dxvalley.crowdfunding.messageManager.email;

public interface EmailService {
    boolean isValidEmail(String email);
    void send(String to, String email, String subject);
}
