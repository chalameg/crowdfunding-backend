package com.dxvalley.crowdfunding.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignLikeDTO {
    @NotNull
    private Long campaignId;
    private Long userId;
}
