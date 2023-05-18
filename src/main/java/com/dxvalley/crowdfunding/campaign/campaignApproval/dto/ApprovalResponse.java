package com.dxvalley.crowdfunding.campaign.campaignApproval.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data

public class ApprovalResponse {
    private Long id;
    private String campaignTitle;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String approvedBy;
    private String approvalStatus;
    private String reason;
    private Double commissionRate;
    private String approvalTime;
    private List<String> approvalFiles;
}
