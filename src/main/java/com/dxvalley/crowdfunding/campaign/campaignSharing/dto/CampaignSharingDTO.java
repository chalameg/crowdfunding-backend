package com.dxvalley.crowdfunding.campaign.campaignSharing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CampaignSharingDTO {
    @NotNull(message = "Campaign ID must not be blank")
    private Long campaignId;
    @NotBlank(message = "Sharing platform must not be blank")
    private String sharingPlatform;
}
