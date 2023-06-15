package com.dxvalley.crowdfunding.campaign.campaignPromotion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findPromotionByCampaignId(Long campaignId);

}
