package com.dxvalley.crowdfunding.services;

import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.models.FundingType;
import com.dxvalley.crowdfunding.models.Promotion;

import java.util.List;


public interface PromotionService {
    Promotion addPromotion (Promotion promotion);
    Promotion editPromotion (Promotion promotion);
    Promotion getPromotionById (Long promotionId);
    List<Promotion> getPromotionByCampaign(Long campaignId);
    List<Promotion> getPromotions();
    void deletePromotion(Long promotionId);

}





