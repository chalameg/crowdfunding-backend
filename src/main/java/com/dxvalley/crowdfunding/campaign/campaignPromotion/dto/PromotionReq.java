package com.dxvalley.crowdfunding.campaign.campaignPromotion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PromotionReq {
    @NotBlank
    private String promotionLink;

    @NotBlank
    private String description;

    @NotBlank
    private Long campaignId;
}
