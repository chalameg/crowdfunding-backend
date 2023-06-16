package com.dxvalley.crowdfunding.campaign.campaignPromotion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PromotionReq {
    @NotBlank(message = "Promotion link must not be blank")
    private String promotionLink;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Campaign ID must not be null")
    private Long campaignId;
}
