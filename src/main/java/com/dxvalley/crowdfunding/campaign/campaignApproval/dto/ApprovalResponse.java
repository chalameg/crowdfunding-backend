package com.dxvalley.crowdfunding.campaign.campaignApproval.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApprovalResponse {
    private Long id;
    private String campaignTitle;
    private String approvedBy;
    private String approvalStatus;
    private String reason;
    private Double commissionRate;
    private String approvalTime;
}
