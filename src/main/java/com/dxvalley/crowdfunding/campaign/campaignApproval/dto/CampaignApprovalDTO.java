package com.dxvalley.crowdfunding.campaign.campaignApproval.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CampaignApprovalDTO {
    private Long campaignId;
    private String approvedBy;
    private String approvalStatus;
    private String reason;
    private Double commissionRate;
    private List<MultipartFile> files;
}

