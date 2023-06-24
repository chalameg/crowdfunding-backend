package com.dxvalley.crowdfunding.admin.statistics;

import lombok.Data;
@Data
public class CampaignStatistics {
    private Integer totalCampaigns;
    private Integer totalActiveCampaigns;
    private Integer donationCampaigns;
    private Integer equityCampaigns;
    private Integer rewardsCampaigns;
}
