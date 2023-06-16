package com.dxvalley.crowdfunding.campaign.campaignSharing.dto;

import com.dxvalley.crowdfunding.campaign.campaignSharing.CampaignSharing;

public class CampaignShareMapper {
    public static CampaignShareResponse toCampaignShareResponse(CampaignSharing campaignSharing) {
        CampaignShareResponse campaignShareResponse = new CampaignShareResponse();
        campaignShareResponse.setId(campaignSharing.getId());
        campaignShareResponse.setUsername(campaignSharing.getUsername());
        campaignShareResponse.setSharingPlatform(campaignSharing.getSharingPlatform());
        campaignShareResponse.setSharingTime(campaignSharing.getSharingTime());
        campaignShareResponse.setShareCount(campaignSharing.getShareCount());

        return campaignShareResponse;
    }
}
