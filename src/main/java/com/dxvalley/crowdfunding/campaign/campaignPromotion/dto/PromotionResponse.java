package com.dxvalley.crowdfunding.campaign.campaignPromotion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionResponse {
    private Long id;
    private String promotionLink;
    private String description;
    private String createdAt;
    private String updatedAt;
}
