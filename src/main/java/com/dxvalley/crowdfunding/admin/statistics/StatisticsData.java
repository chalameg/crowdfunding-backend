package com.dxvalley.crowdfunding.admin.statistics;

import lombok.Data;

@Data
public class StatisticsData {
    private UserStatistics userStatistics;
    private PaymentStatistics paymentStatistics;
    private CampaignStatistics campaignStatistics;

}
