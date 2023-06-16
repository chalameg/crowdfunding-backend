package com.dxvalley.crowdfunding.campaign.campaignSharing.dto;

import lombok.Data;

@Data
public class CampaignShareResponse {
    private Long id;
    private String username;
    private String sharingPlatform;
    private String sharingTime;
    private int shareCount;
}
