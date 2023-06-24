package com.dxvalley.crowdfunding.campaign.campaignSetting;

import com.dxvalley.crowdfunding.campaign.campaignSetting.dto.SettingMapper;
import com.dxvalley.crowdfunding.campaign.campaignSetting.dto.SettingResponse;
import com.dxvalley.crowdfunding.campaign.campaignSetting.dto.SettingUpdateReq;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SettingService {
    private final SettingRepository settingsRepository;
    private final DateTimeFormatter dateTimeFormatter;

    public SettingService(final SettingRepository settingsRepository, final DateTimeFormatter dateTimeFormatter) {
        this.settingsRepository = settingsRepository;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public SettingResponse getSetting() {
        Setting setting = this.settingsRepository.findById(Short.valueOf((short) 1)).orElseThrow(() -> {
            return new RuntimeException("setting not found");
        });
        return SettingMapper.toSettingResponse(setting);
    }

    public Setting getSettingForAdmin() {
        return this.settingsRepository.findById(Short.valueOf((short) 1)).orElseThrow(() -> {
            return new RuntimeException("setting not found");
        });
    }

    public Setting updateSetting(Short id, SettingUpdateReq newSetting) {
        Setting existingSetting = this.settingsRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("Settings not found");
        });
        existingSetting.setMaxCommissionForEquity(newSetting.getMaxCommissionForEquity());
        existingSetting.setMinCommissionForEquity(newSetting.getMinCommissionForEquity());
        existingSetting.setMaxCommissionForDonations(newSetting.getMaxCommissionForDonations());
        existingSetting.setMinCommissionForDonations(newSetting.getMinCommissionForDonations());
        existingSetting.setMaxCommissionForRewards(newSetting.getMaxCommissionForRewards());
        existingSetting.setMinCommissionForRewards(newSetting.getMinCommissionForRewards());
        existingSetting.setMaxCampaignGoal(newSetting.getMaxCampaignGoal());
        existingSetting.setMinCampaignGoal(newSetting.getMinCampaignGoal());
        existingSetting.setMaxCampaignDuration(newSetting.getMaxCampaignDuration());
        existingSetting.setMinCampaignDuration(newSetting.getMinCampaignDuration());
        existingSetting.setMinDonationAmount(newSetting.getMinDonationAmount());
        existingSetting.setUpdatedBy(newSetting.getUpdatedBy());
        existingSetting.setUpdatedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        return this.settingsRepository.save(existingSetting);
    }
}