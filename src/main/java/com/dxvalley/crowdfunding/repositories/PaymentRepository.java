package com.dxvalley.crowdfunding.repositories;

import com.dxvalley.crowdfunding.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long>{
    Payment findPaymentByBankAccount(String bankAccount);
    @Query("SELECT new Payment(p.paymentId, p.bankAccount)" +
            " from Payment as p WHERE p.campaign.campaignId = :campaignId")
    Payment findPaymentByCampaignId(Long campaignId);
}

