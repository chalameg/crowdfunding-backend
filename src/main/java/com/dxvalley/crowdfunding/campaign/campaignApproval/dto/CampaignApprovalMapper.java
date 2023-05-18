package com.dxvalley.crowdfunding.campaign.campaignApproval.dto;

import com.dxvalley.crowdfunding.campaign.campaignApproval.CampaignApproval;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CampaignApprovalMapper {

    private static final List<String> DEFAULT_MESSAGE = Collections.singletonList("No document was used to approve this campaign.");

    public ApprovalResponse toAdminApprovalResponse(CampaignApproval campaignApproval) {
        ApprovalResponse approvalResponse = toApprovalResponse(campaignApproval);
        approvalResponse.setApprovedBy(campaignApproval.getApprovedBy());
        approvalResponse.setApprovalFiles(campaignApproval.getApprovalFiles() != null ? campaignApproval.getApprovalFiles() : DEFAULT_MESSAGE);
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
