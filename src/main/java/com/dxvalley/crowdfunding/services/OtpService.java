package com.dxvalley.crowdfunding.services;

import com.dxvalley.crowdfunding.models.Otp;

public interface OtpService {
    Otp addOtp (Otp otp);
    void deleteOtp( Long otpId);
}
