package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.Otp;

public interface OtpRepository extends JpaRepository<Otp,Long> {
    Otp findOtpByPhoneNumber(String phoneNumber);  
}
