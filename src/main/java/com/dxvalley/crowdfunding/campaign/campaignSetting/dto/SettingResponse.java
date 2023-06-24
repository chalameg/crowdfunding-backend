package com.dxvalley.crowdfunding.campaign.campaignSetting.dto;

import lombok.Data;

@Data
public class SettingResponse {
    private Short id;
    private Double maxCommissionForEquity;
    private Double minCommissionForEquity;
    private Double maxCommissionForDonations;
    private Double minCommissionForDonations;
    private Double maxCommissionForRewards;
    private Double minCommissionForRewards;
    private Double maxCampaignGoal;
    private Double minCampaignGoal;
    private Integer maxCampaignDuration;
    private Integer minCampaignDuration;
    private Double minDonationAmount;

}
