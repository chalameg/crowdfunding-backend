package com.dxvalley.crowdfunding.campaign.campaignSetting;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private String updatedAt;
    private String updatedBy;
}
