package com.dxvalley.crowdfunding.campaign.campaignApproval.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CampaignApprovalDTO {
    @NotNull(message = "Campaign ID cannot be null")
    private Long campaignId;
    @NotEmpty(message = "approvedBy name cannot be empty")
    private String approvedBy;
    @Pattern(regexp = "(?i)^(accepted|rejected)$", message = "Approval status must be either 'accepted' or 'rejected'")
    @NotEmpty(message = "Approval status cannot be empty")
    private String approvalStatus;
    @NotEmpty(message = "Reason cannot be empty")
    private String reason;
    @Positive(message = "Commission rate must be a positive number")
    private Double commissionRate;
}

