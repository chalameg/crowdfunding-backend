package com.dxvalley.crowdfunding.paymentManager.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findPaymentByOrderId(String orderId);

    List<Payment> findByCampaignIdAndPaymentStatus(Long campaignId, PaymentStatus paymentStatus);

    List<Payment> findByUserUserId(Long userId);

    @Query("SELECT SUM(p.amount) FROM Payment as p WHERE p.campaign.id = :campaignId")
    Double findTotalAmountOfPaymentForCampaign(Long campaignId);

    @Query("SELECT SUM(p.amount) FROM Payment as p")
    Double findTotalAmountRaisedOnPlatform();

    List<Payment> findByPaymentStatus(String status);

    List<Payment> findByCampaignId(Long campaignId);
}
