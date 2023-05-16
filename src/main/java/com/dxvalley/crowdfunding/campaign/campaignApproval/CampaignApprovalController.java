package com.dxvalley.crowdfunding.campaign.campaignApproval;

import com.dxvalley.crowdfunding.utils.ApiResponse;
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
    public ResponseEntity<?> getCampaignApprovalByCampaignId(@PathVariable Long id) {
        return ApiResponse.success(campaignApprovalService.getCampaignApprovalByCampaignId(id));
    }
}
