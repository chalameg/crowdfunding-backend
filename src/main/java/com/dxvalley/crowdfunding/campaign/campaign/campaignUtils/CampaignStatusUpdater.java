package com.dxvalley.crowdfunding.campaign.campaign.campaignUtils;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.exception.DatabaseAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignStatusUpdater {
    private final CampaignRepository campaignRepository;
    private final DateTimeFormatter dateTimeFormatter;

    /**
     * Asynchronously updates the campaign status for all campaigns whose expiration date has passed.
     *
     * @return a CompletableFuture representing the completion of the update operation
     */
    @Async
    public CompletableFuture<Void> updateCampaignStatus() {
        try {
            List<Campaign> campaigns = campaignRepository.findCampaignsByCampaignStage(CampaignStage.FUNDING);

            LocalDateTime currentDateTime = LocalDateTime.now();
            for (Campaign campaign : campaigns) {
                LocalDateTime expiredAt = LocalDateTime.parse(campaign.getExpiredAt(), dateTimeFormatter);
                if (expiredAt.isBefore(currentDateTime)) {
                    campaign.setCampaignStage(CampaignStage.COMPLETED);
                }
            }

            campaignRepository.saveAll(campaigns);
            return CompletableFuture.completedFuture(null);
        } catch (DataAccessException ex) {
            log.error("An error occurred in {}.{} while accessing the database. Details: {}",
                    getClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    ex.getMessage());

            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }
}
