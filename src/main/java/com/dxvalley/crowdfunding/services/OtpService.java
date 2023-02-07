package com.dxvalley.crowdfunding.services;

import com.dxvalley.crowdfunding.models.Otp;
import org.aspectj.weaver.loadtime.Options;
import org.springframework.http.ResponseEntity;

public interface OtpService {
    Otp addOtp (Otp otp);
    ResponseEntity<?> sendOtp(String phoneNumber,String randomNumber);
    Otp getOtpByCode(String code);
    void deleteOtp( Long otpId);
}
