package com.dxvalley.crowdfunding.service;

import com.dxvalley.crowdfunding.model.Promotion;

import java.util.List;


public interface PromotionService {
    Promotion addPromotion (Promotion promotion);
    Promotion editPromotion (Promotion promotion);
    Promotion getPromotionById (Long promotionId);
    List<Promotion> getPromotionByCampaign(Long campaignId);
    List<Promotion> getPromotions();
    void deletePromotion(Long promotionId);

}





