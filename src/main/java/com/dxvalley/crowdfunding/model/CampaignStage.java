package com.dxvalley.crowdfunding.model;

import java.util.Arrays;

public enum CampaignStage {
    INITIAL,
    PENDING,
    FUNDING,
    REJECTED,
    PAUSED,
    COMPLETED;

    public static CampaignStage lookup(String campaignStage) {
        return Arrays.stream(CampaignStage.values())
                .filter(e -> e.name().equalsIgnoreCase(campaignStage)).findAny()
                .orElseThrow(()-> new IllegalArgumentException("Invalid Campaign Stage."));
    }
}
