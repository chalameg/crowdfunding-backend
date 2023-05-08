package com.dxvalley.crowdfunding.campaign.campaignUpdate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CampaignUpdateDTO {
    @NotNull(message = "Title is required")
    private String title;
    @NotNull(message = "Description is required")
    private String description;
    @NotNull(message = "authorID is required")
    private Long authorID;
    @NotNull(message = "Campaign ID is required")
    private Long campaignId;
}