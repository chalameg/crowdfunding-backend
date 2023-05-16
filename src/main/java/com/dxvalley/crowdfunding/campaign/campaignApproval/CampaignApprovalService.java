package com.dxvalley.crowdfunding.campaign.campaignApproval;

import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;

public interface CampaignApprovalService {
    ApprovalResponse getCampaignApprovalByCampaignId(Long id);

}

