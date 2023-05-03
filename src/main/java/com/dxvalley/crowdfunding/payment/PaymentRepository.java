package com.dxvalley.crowdfunding.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findPaymentByOrderId(String orderId);

    @Query("SELECT new Payment(p.paymentId, p.amount, p.payerFullName,p.transactionCompletedDate,p.isAnonymous,p.currency,p.paymentStatus)" +
            " FROM Payment as p WHERE p.campaign.campaignId = :campaignId AND paymentStatus = :paymentStatus")
    List<Payment> findPaymentsByCampaignCampaignIdAndPaymentStatus(Long campaignId, String paymentStatus);

    @Query("SELECT new Payment(p.paymentId, p.amount, p.payerFullName,p.transactionCompletedDate,p.isAnonymous,p.currency,p.paymentStatus)" +
            " FROM Payment as p WHERE p.user.userId = :userId")
    List<Payment> findPaymentsByUserUserId(Long userId);

    @Query("SELECT SUM(p.amount) FROM Payment as p WHERE p.campaign.campaignId = :campaignId")
    Double findTotalAmountOfPaymentForCampaign(Long campaignId);

    @Query("SELECT SUM(p.amount) FROM Payment as p")
    Double findTotalAmountRaisedOnPlatform();

    List<Payment> findByPaymentStatus(String status);
}
