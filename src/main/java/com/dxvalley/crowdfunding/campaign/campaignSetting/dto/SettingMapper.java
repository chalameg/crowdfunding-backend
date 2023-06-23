package com.dxvalley.crowdfunding.campaign.campaignSetting.dto;

import com.dxvalley.crowdfunding.campaign.campaignSetting.Setting;

public class SettingMapper {


    public static SettingResponse toSettingResponse(Setting setting) {
        SettingResponse settingResponse = new SettingResponse();
        settingResponse.setId(setting.getId());
        settingResponse.setMaxCommissionForEquity(setting.getMaxCommissionForEquity());
        settingResponse.setMinCommissionForEquity(setting.getMinCommissionForEquity());
        settingResponse.setMaxCommissionForDonations(setting.getMaxCommissionForDonations());
        settingResponse.setMinCommissionForDonations(setting.getMinCommissionForDonations());
        settingResponse.setMaxCommissionForRewards(setting.getMaxCommissionForRewards());
        settingResponse.setMinCommissionForRewards(setting.getMinCommissionForRewards());
        settingResponse.setMaxCampaignGoal(setting.getMaxCampaignGoal());
        settingResponse.setMinCampaignGoal(setting.getMinCampaignGoal());
        settingResponse.setMaxCampaignDuration(setting.getMaxCampaignDuration());
        settingResponse.setMinCampaignDuration(setting.getMinCampaignDuration());
        settingResponse.setMinDonationAmount(setting.getMinDonationAmount());
        return settingResponse;
    }
}
