package com.dxvalley.crowdfunding.configuration;

import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignStatusUpdater;
import com.dxvalley.crowdfunding.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableAsync
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTasksConfig {
    private final CampaignStatusUpdater campaignStatusUpdater;
    private final PaymentService paymentService;

    /**
     * Scheduled method that updates the campaign status for all campaigns whose expiration date has passed.
     * This method is executed every 6 hours.
     */
    @Scheduled(fixedDelay = 21600000) // runs every 6 hours
    public void updateCampaignStatus() {
        campaignStatusUpdater.updateCampaignStatus();
    }

    /**
     * Scheduled method that checks the payment status of all Chapa payments at a fixed rate.
     * This method is executed every 30 minutes.
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes = 30 * 60 * 1000 = 1800000 milliseconds
    public void chapaPaymentStatusChecker() {
        paymentService.chapaPaymentStatusChecker();
    }
}
