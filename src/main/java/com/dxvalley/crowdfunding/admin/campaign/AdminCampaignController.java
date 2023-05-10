package com.dxvalley.crowdfunding.admin.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.CampaignService;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/campaigns")
@RequiredArgsConstructor
public class AdminCampaignController {
    private final CampaignService campaignService;
    private final AdminCampaignService adminCampaignService;

    @GetMapping
    ResponseEntity<?> getCampaigns() {
        return new ResponseEntity<>(adminCampaignService.getCampaigns(), HttpStatus.OK);
    }

    @GetMapping("campaignId/{campaignId}")
    ResponseEntity<?> getCampaign(@PathVariable Long campaignId) {
        return new ResponseEntity<>(campaignService.getCampaignById(campaignId), HttpStatus.OK);
    }

    @GetMapping("/campaignOwner/{owner}")
    ResponseEntity<?> getCampaignByOwner(@PathVariable String owner) {
        return new ResponseEntity<>(campaignService.getCampaignsByOwner(owner), HttpStatus.OK);
    }

    @GetMapping("/campaignsCategory/{categoryId}")
    ResponseEntity<?> getCampaignsByCategory(@PathVariable Long categoryId) {
        return new ResponseEntity<>(campaignService.getCampaignByCategory(categoryId), HttpStatus.OK);
    }

    @GetMapping("/campaignsSubCategory/{subCategoryId}")
    ResponseEntity<?> getCampaignsBySubCategory(@PathVariable Long subCategoryId) {
        return new ResponseEntity<>(campaignService.getCampaignBySubCategory(subCategoryId), HttpStatus.OK);
    }

    @GetMapping("/campaignsStage/{campaignStage}")
    ResponseEntity<?> getCampaignsByStage(@PathVariable String campaignStage) {
        return new ResponseEntity<>(campaignService.getCampaignsByStage(campaignStage), HttpStatus.OK);
    }

    @GetMapping("/campaignsFundingType/{fundingTypeId}")
    ResponseEntity<?> getCampaignsByFundingType(@PathVariable Long fundingTypeId) {
        return new ResponseEntity<>(campaignService.getCampaignsByFundingType(fundingTypeId), HttpStatus.OK);
    }

    @GetMapping("/searchCampaigns")
    ResponseEntity<?> searchCampaigns(@RequestParam String searchParam) {
        return new ResponseEntity<>(campaignService.searchCampaigns(searchParam), HttpStatus.OK);
    }

    @PutMapping("edit/{campaignId}")
    ResponseEntity<?> editCampaign(@PathVariable Long campaignId, @RequestBody CampaignDTO campaignDTO) {
        return new ResponseEntity<>(campaignService.editCampaign(campaignId, campaignDTO), HttpStatus.OK);
    }

    @PutMapping("accept-reject/{campaignId}")
    ResponseEntity<?> acceptRejectCampaign(@PathVariable Long campaignId, @RequestParam Boolean isAccepted) {
        return new ResponseEntity<>(adminCampaignService.acceptRejectCampaign(campaignId,isAccepted), HttpStatus.OK);
    }

    @PutMapping("suspend-resume/{campaignId}")
    ResponseEntity<?> suspendResumeCampaign(@PathVariable Long campaignId) {
        return new ResponseEntity<>(adminCampaignService.suspendResumeCampaign(campaignId), HttpStatus.OK);
    }

    @DeleteMapping("delete/{campaignId}")
    ResponseEntity<?> deleteCampaign(@PathVariable Long campaignId) throws ResourceNotFoundException {
        campaignService.deleteCampaign(campaignId);
        return ApiResponse.success( "Campaign successfully deleted!");
    }

}
