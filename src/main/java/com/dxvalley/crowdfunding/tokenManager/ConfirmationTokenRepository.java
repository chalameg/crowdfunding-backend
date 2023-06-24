package com.dxvalley.crowdfunding.tokenManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);

    @Query("SELECT c FROM ConfirmationToken c WHERE c.user.username = :phoneNumber")
    ConfirmationToken findOtpByPhoneNumber(String phoneNumber);

    @Query("SELECT c FROM ConfirmationToken c WHERE c.user.username = :phoneNumber AND c.token =:token")
    ConfirmationToken findOtpByPhoneNumberAndByCode(String phoneNumber, String token);

}
