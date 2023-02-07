package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.Otp;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Long> {
    Otp findOtpByPhoneNumber(String phoneNumber);
    @Query("SELECT o FROM Otp o WHERE o.phoneNumber = ?1 AND o.code = ?2")
    Otp findOtpByPhoneNumberAndByCode(String phoneNumber, String code);

    Optional<Otp> findOtpByCode(String code);
}
