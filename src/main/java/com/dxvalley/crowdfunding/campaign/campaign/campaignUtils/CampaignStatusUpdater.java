package com.dxvalley.crowdfunding.campaign.campaign.campaignUtils;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class CampaignStatusUpdater {
    private final CampaignRepository campaignRepository;
    private final DateTimeFormatter dateTimeFormatter;


    /**
     * Scheduled task that updates the campaign status by checking the expiration time.
     * <p>
     * This task runs every 5 minutes and is executed asynchronously using the "asyncExecutor" thread pool.
     * It retrieves campaigns in the "FUNDING" stage from the repository, compares their expiration time
     * with the current time, and updates the campaign stage to "COMPLETED" if the expiration time has passed.
     * The updated campaigns are then saved back to the repository.
     *
     * @throws DataAccessException if an error occurs while accessing the database.
     */
    @Scheduled(fixedDelay = 300000) // runs every 5 minutes
    @Async("asyncExecutor")
    public void updateCampaignStatus() {
        List<Campaign> campaigns = campaignRepository.findCampaignsByCampaignStage(CampaignStage.FUNDING);

        LocalDateTime currentDateTime = LocalDateTime.now();
        for (Campaign campaign : campaigns) {
            LocalDateTime expiredAt = LocalDateTime.parse(campaign.getCompletedAt(), dateTimeFormatter);
            if (expiredAt.isBefore(currentDateTime)) {
                campaign.setCampaignStage(CampaignStage.COMPLETED);
            }
        }

        campaignRepository.saveAll(campaigns);
    }
}
