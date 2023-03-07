package com.dxvalley.crowdfunding.repositories;

import com.dxvalley.crowdfunding.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findPaymentByOrderId(String orderId);

    List<Payment> findPaymentsByCampaignCampaignId(Long campaignId);

    List<Payment> findPaymentsByUserUserId(Long userId);

    @Query("SELECT SUM(p.amount) FROM Payment as p WHERE p.campaign.campaignId = :campaignId")
    Double findTotalAmountOfPaymentForCampaign(Long campaignId);

    @Query("SELECT SUM(p.amount) FROM Payment as p")
    Double findTotalAmountRaisedOnPlatform();

}
