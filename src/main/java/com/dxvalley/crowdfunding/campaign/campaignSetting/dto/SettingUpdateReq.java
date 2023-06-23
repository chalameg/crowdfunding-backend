package com.dxvalley.crowdfunding.campaign.campaignSetting.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SettingUpdateReq {
    private @NotNull(
            message = "Max commission for equity cannot be null"
    ) @Min(
            value = 0L,
            message = "Max commission for equity must be greater than or equal to 0"
    ) Double maxCommissionForEquity;
    private @NotNull(
            message = "Min commission for equity cannot be null"
    ) @Min(
            value = 0L,
            message = "Min commission for equity must be greater than or equal to 0"
    ) Double minCommissionForEquity;
    private @NotNull(
            message = "Max commission for donations cannot be null"
    ) @Min(
            value = 0L,
            message = "Max commission for donations must be greater than or equal to 0"
    ) Double maxCommissionForDonations;
    private @NotNull(
            message = "Min commission for donations cannot be null"
    ) @Min(
            value = 0L,
            message = "Min commission for donations must be greater than or equal to 0"
    ) Double minCommissionForDonations;
    private @NotNull(
            message = "Max commission for rewards cannot be null"
    ) @Min(
            value = 0L,
            message = "Max commission for rewards must be greater than or equal to 0"
    ) Double maxCommissionForRewards;
    private @NotNull(
            message = "Min commission for rewards cannot be null"
    ) @Min(
            value = 0L,
            message = "Min commission for rewards must be greater than or equal to 0"
    ) Double minCommissionForRewards;
    private @NotNull(
            message = "Max campaign goal cannot be null"
    ) @Min(
            value = 0L,
            message = "Max campaign goal must be greater than or equal to 0"
    ) Double maxCampaignGoal;
    private @NotNull(
            message = "Min campaign goal cannot be null"
    ) @Min(
            value = 0L,
            message = "Min campaign goal must be greater than or equal to 0"
    ) Double minCampaignGoal;
    private @NotNull(
            message = "Max campaign duration cannot be null"
    ) @Min(
            value = 1L,
            message = "Max campaign duration must be greater than 0"
    ) Integer maxCampaignDuration;
    private @NotNull(
            message = "Min campaign duration cannot be null"
    ) @Min(
            value = 1L,
            message = "Min campaign duration must be greater than 0"
    ) Integer minCampaignDuration;
    private @NotNull(
            message = "Minimum donation amount cannot be null"
    ) @Min(
            value = 0L,
            message = "Minimum donation amount must be greater than or equal to 0"
    ) Double minDonationAmount;
    private @NotEmpty String updatedBy;

    public @AssertTrue(
            message = "Max commission for equity must be greater than Min commission for equity"
    ) boolean isValidEquityCommissionRange() {
        return this.maxCommissionForEquity == null || this.minCommissionForEquity == null || this.maxCommissionForEquity > this.minCommissionForEquity;
    }

    public @AssertTrue(
            message = "Max commission for donation must be greater than Min commission for donation"
    ) boolean isValidDonationCommissionRange() {
        return this.maxCommissionForDonations == null || this.minCommissionForDonations == null || this.maxCommissionForDonations > this.minCommissionForDonations;
    }

    public @AssertTrue(
            message = "Max commission for reward must be greater than Min commission for reward"
    ) boolean isValidRewardCommissionRange() {
        return this.maxCommissionForRewards == null || this.minCommissionForRewards == null || this.maxCommissionForRewards > this.minCommissionForRewards;
    }

    public @AssertTrue(
            message = "Max Campaign Goal must be greater than Min Campaign Goal"
    ) boolean isValidCampaignGoalRange() {
        return this.maxCampaignGoal == null || this.minCampaignGoal == null || this.maxCampaignGoal > this.minCampaignGoal;
    }

    public @AssertTrue(
            message = "Max Campaign Duration must be greater than Min Campaign Duration"
    ) boolean isValidCampaignDurationRange() {
        return this.maxCampaignDuration == null || this.minCampaignDuration == null || this.maxCampaignDuration > this.minCampaignDuration;
    }
}
