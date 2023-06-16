package com.dxvalley.crowdfunding.campaign.campaignApproval.dto;

import com.dxvalley.crowdfunding.campaign.campaignApproval.CampaignApproval;

public class CampaignApprovalMapper {

    public static ApprovalResponse toAdminApprovalResponse(CampaignApproval campaignApproval) {
        ApprovalResponse approvalResponse = toApprovalResponse(campaignApproval);
        approvalResponse.setApprovedBy(campaignApproval.getApprovedBy());
        approvalResponse.setApprovalFiles(campaignApproval.getApprovalFiles());
        return approvalResponse;
    }

    public static ApprovalResponse toApprovalResponse(CampaignApproval campaignApproval) {
        ApprovalResponse approvalResponse = new ApprovalResponse();
        approvalResponse.setId(campaignApproval.getId());
        approvalResponse.setCampaignTitle(campaignApproval.getCampaign().getTitle());
        approvalResponse.setApprovalStatus(campaignApproval.getApprovalStatus().name());
        approvalResponse.setReason(campaignApproval.getReason());
        approvalResponse.setCommissionRate(campaignApproval.getCommissionRate());
        approvalResponse.setApprovalTime(campaignApproval.getApprovedAt());
        return approvalResponse;
    }

}
