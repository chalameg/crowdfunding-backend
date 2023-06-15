package com.dxvalley.crowdfunding.campaign.campaignPromotion;

import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionReq;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionRes;
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
    ResponseEntity<Promotion> getPromotion(@PathVariable Long promotionId) {
        Promotion promotion = promotionService.getPromotionById(promotionId);
        return ResponseEntity.ok(promotion);
    }

    @GetMapping("/byCampaign/{promotionId}")
    ResponseEntity<List<PromotionRes>> getPromotionByCampaign(@PathVariable Long promotionId) {
        List<PromotionRes> promotionRes = promotionService.getPromotionByCampaign(promotionId);
        return ResponseEntity.ok(promotionRes);
    }

    @PostMapping("/add")
    ResponseEntity<PromotionRes> addPromotion(@RequestBody @Valid PromotionReq promotionReq) {
        PromotionRes promotionRes = promotionService.addPromotion(promotionReq);
        return ResponseEntity.ok(promotionRes);
    }

    @PutMapping("/edit/{promotionId}")
    ResponseEntity<PromotionRes> editPromotion(@PathVariable Long promotionId, @RequestBody @Valid PromotionUpdateReq promotionUpdateReq) {
        PromotionRes promotionRes = promotionService.editPromotion(promotionId, promotionUpdateReq);
        return ResponseEntity.ok(promotionRes);
    }

    @DeleteMapping("delete/{promotionId}")
    ResponseEntity<ApiResponse> deletePromotion(@PathVariable Long promotionId) {
        return promotionService.deletePromotion(promotionId);
    }
}
