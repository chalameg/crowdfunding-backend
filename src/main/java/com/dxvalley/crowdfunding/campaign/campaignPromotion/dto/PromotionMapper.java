package com.dxvalley.crowdfunding.campaign.campaignPromotion.dto;

import com.dxvalley.crowdfunding.campaign.campaignPromotion.Promotion;

public class PromotionMapper {
    public static PromotionRes toPromotionRes(Promotion promotion) {
        PromotionRes promotionRes = new PromotionRes();
        promotionRes.setId(promotion.getId());
        promotionRes.setPromotionLink(promotion.getPromotionLink());
        promotionRes.setDescription(promotion.getDescription());
        promotionRes.setCreatedAt(promotion.getCreatedAt());
        promotionRes.setUpdatedAt(promotion.getUpdatedAt());

        return promotionRes;
    }
}
