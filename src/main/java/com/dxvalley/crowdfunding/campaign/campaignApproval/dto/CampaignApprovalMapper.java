package com.dxvalley.crowdfunding.campaign.campaignApproval.dto;

import com.dxvalley.crowdfunding.campaign.campaignApproval.CampaignApproval;
import org.springframework.stereotype.Component;

@Component
public class CampaignApprovalMapper {
    public ApprovalResponse toAdminApprovalResponse(CampaignApproval campaignApproval) {
        ApprovalResponse approvalResponse = toApprovalResponse(campaignApproval);
        approvalResponse.setApprovedBy(campaignApproval.getApprovedBy());
        return approvalResponse;
    }

    public ApprovalResponse toApprovalResponse(CampaignApproval campaignApproval) {
        ApprovalResponse approvalResponse = new ApprovalResponse();
        approvalResponse.setId(campaignApproval.getId());
        approvalResponse.setCampaignTitle(campaignApproval.getCampaign().getTitle());
        approvalResponse.setApprovalStatus(campaignApproval.getApprovalStatus().name());
        approvalResponse.setReason(campaignApproval.getReason());
        approvalResponse.setCommissionRate(campaignApproval.getCommissionRate());
        approvalResponse.setApprovalTime(campaignApproval.getApprovalTime());
        return approvalResponse;
    }

}
