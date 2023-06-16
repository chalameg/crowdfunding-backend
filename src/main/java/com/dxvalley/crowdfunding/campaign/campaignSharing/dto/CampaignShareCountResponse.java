package com.dxvalley.crowdfunding.campaign.campaignSharing.dto;

import lombok.Data;

@Data
public class CampaignShareCountResponse {
    private int facebookShares;
    private int tiktokShares;
    private int twitterShares;
    private int telegramShares;
    private int whatsappShares;
    private int linkedinShares;
    private int instagramShares;
    private int otherShares;
}
