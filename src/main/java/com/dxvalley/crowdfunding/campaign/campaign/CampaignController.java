package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignAddDto;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLikeDTO;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

    @GetMapping("/getCampaigns")
    ResponseEntity<?> getCampaigns() {
        return ApiResponse.success(campaignService.getCampaigns());
    }

    @GetMapping("getCampaignById/{campaignId}")
    ResponseEntity<?> getCampaign(@PathVariable Long campaignId) {
        return ApiResponse.success(campaignService.getCampaignById(campaignId));
    }

    @GetMapping("/getCampaignByOwner/{owner}")
    ResponseEntity<?> getCampaignByOwner(@PathVariable String owner) {
        return new ResponseEntity<>(campaignService.getCampaignsByOwner(owner), HttpStatus.OK);
    }

    @GetMapping("/getCampaignsByCategory/{categoryId}")
    ResponseEntity<?> getCampaignsByCategory(@PathVariable Long categoryId) {
        return ApiResponse.success(campaignService.getCampaignByCategory(categoryId));
    }

    @GetMapping("/getCampaignsBySubCategory/{subCategoryId}")
    ResponseEntity<?> getCampaignsBySubCategory(@PathVariable Long subCategoryId) {
        return ApiResponse.success(campaignService.getCampaignBySubCategory(subCategoryId));
    }

    @GetMapping("/getCampaignsByStage/{campaignStage}")
    ResponseEntity<?> getCampaignsByStage(@PathVariable String campaignStage) {
        return new ResponseEntity<>(campaignService.getCampaignsByStage(campaignStage), HttpStatus.OK);
    }

    @GetMapping("/getCampaignsByFundingType/{fundingTypeId}")
    ResponseEntity<?> getCampaignsByFundingType(@PathVariable Long fundingTypeId) {
        return ApiResponse.success(campaignService.getCampaignsByFundingType(fundingTypeId));
    }

    @GetMapping("/searchCampaigns")
    ResponseEntity<?> searchCampaigns(@RequestParam String searchParam) {
        return ApiResponse.success(campaignService.searchCampaigns(searchParam));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCampaign(@Valid @RequestBody CampaignAddDto campaignAddRequestDto) {
        return ApiResponse.created(campaignService.addCampaign(campaignAddRequestDto));
    }

    @PostMapping("/like")
    public ResponseEntity<?> likeCampaign(@RequestBody @Valid CampaignLikeDTO campaignLikeDTO) {
        return ApiResponse.success(campaignService.likeCampaign(campaignLikeDTO));
    }

    @PutMapping("edit/{campaignId}")
    ResponseEntity<?> editCampaign(@PathVariable Long campaignId, @RequestBody CampaignDTO campaignDTO) {
        return ApiResponse.success(campaignService.editCampaign(campaignId, campaignDTO));
    }

    @PutMapping("uploadMedias/{campaignId}")
    ResponseEntity<?> uploadMedias(
            @PathVariable Long campaignId,
            @RequestParam(required = false) MultipartFile campaignImage,
            @RequestParam(required = false) String campaignVideo) {
        return ApiResponse.success(campaignService.uploadMedias(campaignId, campaignImage, campaignVideo));
    }

    @PutMapping("uploadFiles/{campaignId}")
    ResponseEntity<?> uploadFiles(@PathVariable Long campaignId, @RequestParam List<MultipartFile> files) {
        return ApiResponse.success(campaignService.uploadFiles(campaignId, files));
    }

    @PutMapping("submit-withdraw/{campaignId}")
    ResponseEntity<?> submitWithdrawCampaign(@PathVariable Long campaignId, @RequestParam String action) {
        if (action.equalsIgnoreCase("SUBMIT")) {
            CampaignDTO campaignDTO = campaignService.submitCampaign(campaignId);
            return ApiResponse.success(campaignDTO);
        } else if (action.equalsIgnoreCase("WITHDRAW")) {
            CampaignDTO campaignDTO = campaignService.withdrawCampaign(campaignId);
            return ApiResponse.success(campaignDTO);
        } else
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid action. Action should be either 'SUBMIT' or 'WITHDRAW'.");
    }

    @PutMapping("pause-resume/{campaignId}")
    ResponseEntity<?> pauseResumeCampaign(@PathVariable Long campaignId, @RequestParam String action) {
        if (action.equalsIgnoreCase("PAUSE")) {
            CampaignDTO campaignDTO = campaignService.pauseCampaign(campaignId);
            return ApiResponse.success(campaignDTO);
        } else if (action.equalsIgnoreCase("RESUME")) {
            CampaignDTO campaignDTO = campaignService.resumeCampaign(campaignId);
            return ApiResponse.success(campaignDTO);
        } else
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid action. Action should be either 'PAUSE' or 'RESUME'.");
    }

    @DeleteMapping("delete/{campaignId}")
    ResponseEntity<?> deleteCampaign(@PathVariable Long campaignId) {
        campaignService.deleteCampaign(campaignId);
        return ApiResponse.success("Campaign successfully deleted!");
    }

}
