package com.dxvalley.crowdfunding.repositories;

import com.dxvalley.crowdfunding.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT new Payment(p.paymentId, p.payerFullName, " +
            "p.transactionOrderDate, p.orderId,p.paymentStatus, p.amount,p.isAnonymous)" +
            "FROM Payment as p WHERE p.campaign.campaignId = :campaignId")
    List<Payment> findPaymentByCampaignId(Long campaignId);
    @Query("SELECT new Payment(p.paymentId, p.payerFullName, " +
            "p.transactionOrderDate, p.orderId,p.paymentStatus, p.amount,p.isAnonymous)"+
            " FROM Payment as p WHERE p.user.userId = :userId")
    List<Payment> findPaymentByUserId(Long userId);

    @Query("SELECT SUM(p.amount) FROM Payment as p WHERE p.campaign.campaignId = :campaignId")
    Double findTotalAmountOfPaymentForCampaign(Long campaignId);

    @Query("SELECT SUM(p.amount) FROM Payment as p")
    Double findTotalAmountRaisedOnPlatform();
}
