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
    @GetMapping("/{id}")
    public ResponseEntity<?> getCampaignApprovalByCampaignId(@PathVariable Long id) {
        return ApiResponse.success(adminCampaignApprovalService.getCampaignApprovalByCampaignId(id));
    }
    @PostMapping
    public ResponseEntity<?> createCampaignApproval(@RequestBody @Valid CampaignApprovalDTO campaignApprovalDTO) {
        return adminCampaignApprovalService.approveCampaign(campaignApprovalDTO);
    }
}
