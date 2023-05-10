package com.dxvalley.crowdfunding.campaign.campaign;

import java.util.Arrays;

public enum CampaignStage {
    INITIAL,
    PENDING,
    FUNDING,
    REJECTED,
    PAUSED,
    COMPLETED,
    SUSPENDED,
    TIME_PASSED;

    public static CampaignStage lookup(String campaignStage) {
        return Arrays.stream(CampaignStage.values())
                .filter(e -> e.name().equalsIgnoreCase(campaignStage)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Campaign Stage."));
    }
}
