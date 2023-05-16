package com.dxvalley.crowdfunding.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.exception.DatabaseAccessException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignUtils {
    private final CampaignRepository campaignRepository;

    public Campaign utilGetCampaignById(Long campaignId) {
        try {
            return campaignRepository.findCampaignByCampaignId(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException("There is no campaign with this ID."));
        } catch (DataAccessException ex) {
            logError("utilGetCampaignById", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }

    public Campaign utilGetPendingCampaignById(Long campaignId, CampaignStage campaignStage) {
        try {
            return campaignRepository.findCampaignByCampaignIdAndCampaignStage(campaignId, campaignStage)
                    .orElseThrow(() -> new ResourceNotFoundException("No pending campaign found with the provided ID. Please check the ID and try again."));
        } catch (DataAccessException ex) {
            logError("utilGetPendingCampaignById", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }

    public void utilUpdateCampaign(Campaign campaign) {
        try {
            campaignRepository.save(campaign);
        } catch (DataAccessException ex) {
            logError("utilUpdateCampaign", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }

    private void logError(String methodName, DataAccessException ex) {
        log.error("An error occurred in {}.{} while accessing the database. Details: {}",
                getClass().getSimpleName(),
                methodName,
                ex.getMessage());
    }
}