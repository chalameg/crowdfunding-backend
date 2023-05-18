package com.dxvalley.crowdfunding.admin.campaign.approval;

import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalDTO;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/campaign-approvals")
public class AdminCampaignApprovalController {
    private final AdminCampaignApprovalService adminCampaignApprovalService;

    @GetMapping("/{campaignId}")
    public ResponseEntity<?> getCampaignApprovalByCampaignId(@PathVariable Long campaignId) {
        return ApiResponse.success(adminCampaignApprovalService.getCampaignApprovalByCampaignId(campaignId));
    }

    @PostMapping
    ResponseEntity<?> approveCampaign(@Valid @RequestParam CampaignApprovalDTO campaignApprovalDTO) {
        return adminCampaignApprovalService.approveCampaign(campaignApprovalDTO);
    }

    @PutMapping
    public ResponseEntity<?> addCampaignApprovalFiles(@RequestParam Long campaignId) {
        System.err.println("we are here bitch");
        return ApiResponse.success("hello");
    }
}
