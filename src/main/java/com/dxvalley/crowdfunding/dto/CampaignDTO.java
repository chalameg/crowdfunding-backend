package com.dxvalley.crowdfunding.dto;

import com.dxvalley.crowdfunding.models.CampaignStage;

public record CampaignDTO(
        Long campaignId,
        String title,
        String shortDescription,
        String city,
        String imageUrl,
        Double goalAmount,
        Short campaignDuration,
        String projectType,
        CampaignStage campaignStage
) {
}
