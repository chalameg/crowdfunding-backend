package com.dxvalley.crowdfunding.campaign.campaignReward.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RewardResponse {
    private Long id;
    private String title;
    private String description;
    private Double amountToCollect;
    private String deliveryTime;
    private String createdAt;
    private String editedAt;
}
