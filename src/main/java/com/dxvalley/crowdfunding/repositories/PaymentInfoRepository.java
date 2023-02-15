package com.dxvalley.crowdfunding.repositories;

import com.dxvalley.crowdfunding.models.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
    @Query("SELECT new PaymentInfo(p.paymentInfoId, p.paidAt, p.amount, p.user)" +
            " FROM PaymentInfo as p WHERE p.campaign.campaignId = :campaignId")
    List<PaymentInfo> findPaymentInfoByCampaignId(Long campaignId);

    @Query("SELECT SUM(p.amount) FROM PaymentInfo as p WHERE p.campaign.campaignId = :campaignId")
    Double findTotalAmountOfPaymentForCampaign(Long campaignId);

    @Query("SELECT new PaymentInfo(p.paymentInfoId, p.paidAt, p.amount, p.user)" +
            " FROM PaymentInfo as p WHERE p.user.userId = :userId")
    List<PaymentInfo> findPaymentInfoByUserId(Long userId);

    @Query("SELECT SUM(p.amount) FROM PaymentInfo as p")
    Double findTotalAmountRaisedOnPlatform();
}
