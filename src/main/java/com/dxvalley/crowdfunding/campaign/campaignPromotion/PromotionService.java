package com.dxvalley.crowdfunding.campaign.campaignPromotion;

import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionReq;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionResponse;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionUpdateReq;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface PromotionService {
    PromotionResponse addPromotion(PromotionReq promotionReq);

    PromotionResponse editPromotion(Long id, PromotionUpdateReq promotionUpdateReq);

    PromotionResponse getPromotionById(Long promotionId);

    List<PromotionResponse> getPromotionByCampaign(Long campaignId);

    ResponseEntity<ApiResponse> deletePromotion(Long promotionId);
}





