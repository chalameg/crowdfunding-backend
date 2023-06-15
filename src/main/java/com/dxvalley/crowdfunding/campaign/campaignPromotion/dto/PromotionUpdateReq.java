package com.dxvalley.crowdfunding.campaign.campaignPromotion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PromotionUpdateReq {
    @NotBlank
    private String description;
}
