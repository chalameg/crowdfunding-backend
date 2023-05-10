package com.dxvalley.crowdfunding.campaign.campaignLike;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignLikeDTO {
    @NotNull
    private Long campaignId;
    @NotNull
    private Long userId;
}
