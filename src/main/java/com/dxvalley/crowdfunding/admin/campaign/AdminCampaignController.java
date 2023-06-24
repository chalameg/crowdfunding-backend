package com.dxvalley.crowdfunding.admin.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignOperationService;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRetrievalService;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalReq;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/campaigns")
@RequiredArgsConstructor
public class AdminCampaignController {
    private final CampaignOperationService campaignOperationService;
    private final CampaignRetrievalService campaignRetrievalService;
    private final AdminCampaignService adminCampaignService;
    private final AdminCampaignApprovalService adminCampaignApprovalService;

    @GetMapping({"/approvals/{campaignId}"})
    public ResponseEntity<ApprovalResponse> getCampaignApprovalByCampaignId(@PathVariable Long campaignId) {
        return ResponseEntity.ok(this.adminCampaignApprovalService.getCampaignApprovalByCampaignId(campaignId));
    }

    @PostMapping({"/approvals"})
    public ResponseEntity<String> approveCampaign(@ModelAttribute @Valid CampaignApprovalReq campaignApprovalReq) {
        return this.adminCampaignApprovalService.approveCampaign(campaignApprovalReq);
    }

    @GetMapping
    ResponseEntity<List<Campaign>> getCampaigns() {
        return ResponseEntity.ok(this.adminCampaignService.getCampaigns());
    }

    @GetMapping({"campaignId/{campaignId}"})
    ResponseEntity<CampaignDTO> getCampaign(@PathVariable Long campaignId) {
        return ResponseEntity.ok(this.campaignRetrievalService.getCampaignById(campaignId));
    }

    @GetMapping({"/campaignOwner/{owner}"})
    ResponseEntity<List<CampaignDTO>> getCampaignByOwner(@PathVariable String owner) {
        return ResponseEntity.ok(this.campaignRetrievalService.getCampaignsByOwner(owner));
    }

    @GetMapping({"/campaignsCategory/{categoryId}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(this.campaignRetrievalService.getCampaignByCategory(categoryId));
    }

    @GetMapping({"/campaignsSubCategory/{subCategoryId}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsBySubCategory(@PathVariable Long subCategoryId) {
        return ResponseEntity.ok(this.campaignRetrievalService.getCampaignBySubCategory(subCategoryId));
    }

    @GetMapping({"/campaignsStage/{campaignStage}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsByStage(@PathVariable String campaignStage) {
        return ResponseEntity.ok(this.campaignRetrievalService.getCampaignsByStage(campaignStage));
    }

    @GetMapping({"/campaignsFundingType/{fundingTypeId}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsByFundingType(@PathVariable Long fundingTypeId) {
        return ResponseEntity.ok(this.campaignRetrievalService.getCampaignsByFundingType(fundingTypeId));
    }

    @GetMapping({"/searchCampaigns"})
    ResponseEntity<List<CampaignDTO>> searchCampaigns(@RequestParam String searchParam) {
        return ResponseEntity.ok(this.campaignRetrievalService.searchCampaigns(searchParam));
    }

    @PutMapping({"suspend-resume/{campaignId}"})
    ResponseEntity<?> suspendResumeCampaign(@PathVariable Long campaignId, @RequestParam String action) {
        CampaignDTO campaignDTO;
        if (action.equalsIgnoreCase("SUSPEND")) {
            campaignDTO = this.adminCampaignService.suspendCampaign(campaignId);
            return ResponseEntity.ok(campaignDTO);
        } else if (action.equalsIgnoreCase("RESUME")) {
            campaignDTO = this.adminCampaignService.resumeCampaign(campaignId);
            return ResponseEntity.ok(campaignDTO);
        } else {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid action. Action should be either 'SUSPEND' or 'RESUME'.");
        }
    }

    @DeleteMapping({"delete/{campaignId}"})
    ResponseEntity<ApiResponse> deleteCampaign(@PathVariable Long campaignId) {
        this.campaignOperationService.deleteCampaign(campaignId);
        return ApiResponse.success("Campaign successfully deleted!");
    }
}
