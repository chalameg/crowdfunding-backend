package com.dxvalley.crowdfunding.campaign.campaignPromotion;

import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionReq;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionRes;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionUpdateReq;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface PromotionService {
    PromotionRes addPromotion(PromotionReq promotionReq);

    PromotionRes editPromotion(Long id, PromotionUpdateReq promotionUpdateReq);

    Promotion getPromotionById(Long promotionId);

    List<PromotionRes> getPromotionByCampaign(Long campaignId);

    ResponseEntity<ApiResponse> deletePromotion(Long promotionId);

}





