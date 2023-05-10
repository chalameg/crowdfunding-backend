package com.dxvalley.crowdfunding.messageManager.sms;

public interface SmsService {
    void sendOtp(String phoneNumber, String randomNumber);
    boolean isValidPhoneNumber(String phoneNumber);
}
