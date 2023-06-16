package com.dxvalley.crowdfunding.campaign.campaignPromotion;

import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionReq;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionResponse;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionUpdateReq;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping("/{promotionId}")
    ResponseEntity<PromotionResponse> getPromotion(@PathVariable Long promotionId) {
        PromotionResponse promotion = promotionService.getPromotionById(promotionId);
        return ResponseEntity.ok(promotion);
    }

    @GetMapping("/byCampaign/{campaignId}")
    ResponseEntity<List<PromotionResponse>> getPromotionByCampaign(@PathVariable Long campaignId) {
        List<PromotionResponse> promotionRes = promotionService.getPromotionByCampaign(campaignId);
        return ResponseEntity.ok(promotionRes);
    }

    @PostMapping("/add")
    ResponseEntity<PromotionResponse> addPromotion(@RequestBody @Valid PromotionReq promotionReq) {
        PromotionResponse promotionResponse = promotionService.addPromotion(promotionReq);
        return ResponseEntity.ok(promotionResponse);
    }

    @PutMapping("/edit/{promotionId}")
    ResponseEntity<PromotionResponse> editPromotion(@PathVariable Long promotionId, @RequestBody @Valid PromotionUpdateReq promotionUpdateReq) {
        PromotionResponse promotionResponse = promotionService.editPromotion(promotionId, promotionUpdateReq);
        return ResponseEntity.ok(promotionResponse);
    }

    @DeleteMapping("/{promotionId}")
    ResponseEntity<ApiResponse> deletePromotion(@PathVariable Long promotionId) {
        return promotionService.deletePromotion(promotionId);
    }
}
