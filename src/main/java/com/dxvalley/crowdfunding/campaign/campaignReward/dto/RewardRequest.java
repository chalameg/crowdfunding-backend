package com.dxvalley.crowdfunding.campaign.campaignReward.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RewardRequest {
    @NotEmpty(message = "title must be provided.")
    private String title;

    @NotEmpty(message = "description must be provided.")
    private String description;
    @NotNull(message = "amountToCollect must be provided.")
    private Double amountToCollect;

    @NotEmpty(message = "deliveryTime must be provided.")
    private String deliveryTime;

    @NotNull(message = "Campaign ID must be provided.")
    private Long campaignId;
    
}