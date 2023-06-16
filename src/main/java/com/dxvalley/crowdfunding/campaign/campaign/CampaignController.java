package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignAddReq;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignUpdateReq;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLikeReq;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campaigns")
public class CampaignController {
    private final CampaignOperationService campaignOperationService;
    private final CampaignRetrievalService campaignRetrievalService;

    @GetMapping("/getCampaigns")
    ResponseEntity<List<CampaignDTO>> getCampaigns() {
        return ResponseEntity.ok(campaignRetrievalService.getCampaigns());
    }

    @GetMapping("getCampaignById/{campaignId}")
    ResponseEntity<CampaignDTO> getCampaign(@PathVariable Long campaignId) {
        return ResponseEntity.ok(campaignRetrievalService.getCampaignById(campaignId));
    }

    @GetMapping("/getCampaignByOwner/{owner}")
    ResponseEntity<List<CampaignDTO>> getCampaignByOwner(@PathVariable String owner) {
        return new ResponseEntity<>(campaignRetrievalService.getCampaignsByOwner(owner), HttpStatus.OK);
    }

    @GetMapping("/getCampaignsByCategory/{categoryId}")
    ResponseEntity<List<CampaignDTO>> getCampaignsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(campaignRetrievalService.getCampaignByCategory(categoryId));
    }

    @GetMapping("/getCampaignsBySubCategory/{subCategoryId}")
    ResponseEntity<List<CampaignDTO>> getCampaignsBySubCategory(@PathVariable Long subCategoryId) {
        return ResponseEntity.ok(campaignRetrievalService.getCampaignBySubCategory(subCategoryId));
    }

    @GetMapping("/getCampaignsByStage/{campaignStage}")
    ResponseEntity<List<CampaignDTO>> getCampaignsByStage(@PathVariable String campaignStage) {
        return new ResponseEntity<>(campaignRetrievalService.getCampaignsByStage(campaignStage), HttpStatus.OK);
    }

    @GetMapping("/getCampaignsByFundingType/{fundingTypeId}")
    ResponseEntity<List<CampaignDTO>> getCampaignsByFundingType(@PathVariable Long fundingTypeId) {
        return ResponseEntity.ok(campaignRetrievalService.getCampaignsByFundingType(fundingTypeId));
    }

    @GetMapping("/searchCampaigns")
    ResponseEntity<List<CampaignDTO>> searchCampaigns(@RequestParam String searchParam) {
        return ResponseEntity.ok(campaignRetrievalService.searchCampaigns(searchParam));
    }

    @PostMapping("/add")
    public ResponseEntity<Campaign> addCampaign(@Valid @RequestBody CampaignAddReq campaignAddRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignOperationService.addCampaign(campaignAddRequestDto));
    }

    @PostMapping("/like")
    public ResponseEntity<ApiResponse> likeCampaign(@RequestBody @Valid CampaignLikeReq campaignLikeReq) {
        return campaignOperationService.likeCampaign(campaignLikeReq);
    }

    @PutMapping("edit/{campaignId}")
    ResponseEntity<CampaignDTO> editCampaign(@PathVariable Long campaignId, @RequestBody CampaignUpdateReq campaignUpdateReq) {
        return ResponseEntity.ok(campaignOperationService.editCampaign(campaignId, campaignUpdateReq));
    }

    @PutMapping("uploadMedias/{campaignId}")
    ResponseEntity<CampaignDTO> uploadMedias(
            @PathVariable Long campaignId,
            @RequestParam(required = false) MultipartFile campaignImage,
            @RequestParam(required = false) String campaignVideo) {
        return ResponseEntity.ok(campaignOperationService.uploadCampaignMedias(campaignId, campaignImage, campaignVideo));
    }

    @PutMapping("submit-withdraw/{campaignId}")
    ResponseEntity<?> submitWithdrawCampaign(@PathVariable Long campaignId, @RequestParam String action) {
        if (action.equalsIgnoreCase("SUBMIT")) {
            CampaignDTO campaignDTO = campaignOperationService.submitCampaign(campaignId);
            return ResponseEntity.ok(campaignDTO);
        } else if (action.equalsIgnoreCase("WITHDRAW")) {
            CampaignDTO campaignDTO = campaignOperationService.withdrawCampaign(campaignId);
            return ResponseEntity.ok(campaignDTO);
        } else
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid action. Action should be either 'SUBMIT' or 'WITHDRAW'.");
    }

    @PutMapping("pause-resume/{campaignId}")
    ResponseEntity<?> pauseResumeCampaign(@PathVariable Long campaignId, @RequestParam String action) {
        if (action.equalsIgnoreCase("PAUSE")) {
            CampaignDTO campaignDTO = campaignOperationService.pauseCampaign(campaignId);
            return ResponseEntity.ok(campaignDTO);
        } else if (action.equalsIgnoreCase("RESUME")) {
            CampaignDTO campaignDTO = campaignOperationService.resumeCampaign(campaignId);
            return ResponseEntity.ok(campaignDTO);
        } else
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid action. Action should be either 'PAUSE' or 'RESUME'.");
    }

    @DeleteMapping("delete/{campaignId}")
    ResponseEntity<ApiResponse> deleteCampaign(@PathVariable Long campaignId) {
        return campaignOperationService.deleteCampaign(campaignId);
    }

}
