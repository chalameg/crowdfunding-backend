package com.dxvalley.crowdfunding.notification;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public interface OtpService {
    ApiResponse sendOtp(String phoneNumber, String randomNumber);
}
