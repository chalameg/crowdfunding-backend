package com.dxvalley.crowdfunding.campaign.campaignPromotion;

import java.util.List;


public interface PromotionService {
    Promotion addPromotion (Promotion promotion);
    Promotion editPromotion (Promotion promotion);
    Promotion getPromotionById (Long promotionId);
    List<Promotion> getPromotionByCampaign(Long campaignId);
    List<Promotion> getPromotions();
    void deletePromotion(Long promotionId);

}





