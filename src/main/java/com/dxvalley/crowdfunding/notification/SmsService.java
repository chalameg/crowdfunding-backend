package com.dxvalley.crowdfunding.notification;

import com.dxvalley.crowdfunding.dto.ApiResponse;

public interface SmsService {
    ApiResponse sendOtp(String phoneNumber, String randomNumber);

    boolean isValidPhoneNumber(String phoneNumber);
}
