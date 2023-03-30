package com.dxvalley.crowdfunding.notification;

public interface SmsService {
    void sendOtp(String phoneNumber, String randomNumber);

    boolean isValidPhoneNumber(String phoneNumber);
}
