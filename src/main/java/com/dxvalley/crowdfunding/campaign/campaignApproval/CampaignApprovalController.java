package com.dxvalley.crowdfunding.campaign.campaignApproval;

import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/campaign-approvals")
public class CampaignApprovalController {
    private final CampaignApprovalService campaignApprovalService;

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalResponse> getCampaignApprovalByCampaignId(@PathVariable Long id) {
        return ResponseEntity.ok(campaignApprovalService.getCampaignApprovalByCampaignId(id));
    }
}
