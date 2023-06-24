package com.dxvalley.crowdfunding.campaign.campaignApproval;

import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/campaign-approvals"})
public class CampaignApprovalController {
    private final CampaignApprovalService campaignApprovalService;

    public CampaignApprovalController(final CampaignApprovalService campaignApprovalService) {
        this.campaignApprovalService = campaignApprovalService;
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<ApprovalResponse> getCampaignApprovalByCampaignId(@PathVariable Long id) {
        return ResponseEntity.ok(this.campaignApprovalService.getCampaignApprovalByCampaignId(id));
    }
}