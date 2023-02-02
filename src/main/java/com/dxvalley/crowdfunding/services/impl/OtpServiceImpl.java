package com.dxvalley.crowdfunding.services.impl;

import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.models.Otp;
import com.dxvalley.crowdfunding.repositories.OtpRepository;
import com.dxvalley.crowdfunding.services.OtpService;

@Service
public class OtpServiceImpl implements OtpService{
    private final OtpRepository otpRepository;

    public OtpServiceImpl(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }
    
    @Override
    public Otp addOtp(Otp otp) {
        return this.otpRepository.save(otp);
    }

    @Override
    public void deleteOtp(Long otpId) {
        otpRepository.deleteById(otpId);
    }
    
}
