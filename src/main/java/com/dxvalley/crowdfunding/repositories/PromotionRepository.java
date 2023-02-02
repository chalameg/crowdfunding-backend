package com.dxvalley.crowdfunding.repositories;

import com.dxvalley.crowdfunding.models.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Promotion findPromotionByPromotionId(Long promotionId);


    @Query("SELECT new Promotion(p.promotionId, p.promotionLink, p.description)" +
            " FROM Promotion as p WHERE p.campaign.campaignId = :campaignId")
    List<Promotion> findPromotionByCampaignId(Long campaignId);

}
