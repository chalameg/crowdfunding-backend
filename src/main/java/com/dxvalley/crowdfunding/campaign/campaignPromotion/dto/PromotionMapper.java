package com.dxvalley.crowdfunding.campaign.campaignPromotion.dto;

import com.dxvalley.crowdfunding.campaign.campaignPromotion.Promotion;

public class PromotionMapper {
    public static PromotionResponse toPromotionRes(Promotion promotion) {
        PromotionResponse promotionResponse = new PromotionResponse();
        promotionResponse.setId(promotion.getId());
        promotionResponse.setPromotionLink(promotion.getPromotionLink());
        promotionResponse.setDescription(promotion.getDescription());
        promotionResponse.setCreatedAt(promotion.getCreatedAt());
        promotionResponse.setUpdatedAt(promotion.getUpdatedAt());

        return promotionResponse;
    }
}
