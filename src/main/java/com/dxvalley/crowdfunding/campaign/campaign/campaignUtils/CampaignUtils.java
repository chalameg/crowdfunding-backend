package com.dxvalley.crowdfunding.campaign.campaign.campaignUtils;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.CampaignBankAccount;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.CampaignBankAccountRepository;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountDTO;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountMapper;
import com.dxvalley.crowdfunding.exception.DatabaseAccessException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignUtils {
    private final CampaignRepository campaignRepository;
    private final CampaignBankAccountRepository campaignBankAccountRepository;

    public Campaign utilGetCampaignById(Long campaignId) {
        try {
            return campaignRepository.findCampaignByCampaignId(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException("There is no campaign with this ID."));
        } catch (DataAccessException ex) {
            logError("utilGetCampaignById", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }

    public Campaign utilGetCampaignByIdAndStage(Long campaignId, CampaignStage campaignStage) {
        try {
            return campaignRepository.findCampaignByCampaignIdAndCampaignStage(campaignId, campaignStage)
                    .orElseThrow(() -> new ResourceNotFoundException("No pending campaign found with the provided ID. Please check the ID and try again."));
        } catch (DataAccessException ex) {
            logError("utilGetCampaignByIdAndStage", ex);
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

    /**
     * Checks if the provided campaign data is valid.
     *
     * @param campaign The campaign to validate.
     * @return True if the campaign data is valid, false otherwise.
     */
    public boolean isValidCampaign(Campaign campaign) {
        return campaign.getShortDescription() != null && !campaign.getShortDescription().isEmpty()
                && ((campaign.getImageUrl() != null && !campaign.getImageUrl().isEmpty()) || (campaign.getVideoLink() != null && !campaign.getVideoLink().isEmpty()))
                && campaign.getGoalAmount() != null && campaign.getGoalAmount() != 0
                && campaign.getProjectType() != null && !campaign.getProjectType().isEmpty()
                && campaign.getDescription() != null && !campaign.getDescription().isEmpty()
                && campaign.getCampaignDuration() != null && campaign.getCampaignDuration() != 0;
    }

    /**
     * Retrieves the bank account associated with a campaign based on the campaign ID.
     *
     * @param campaignId The ID of the campaign.
     * @return The bank account associated with the campaign, or null if no bank account is found.
     */
    public BankAccountDTO getCampaignBankAccountByCampaignId(Long campaignId) {
        Optional<CampaignBankAccount> campaignBankAccount = campaignBankAccountRepository.findCampaignBankAccountByCampaignCampaignId(campaignId);
        if (campaignBankAccount.isPresent()) {
            return BankAccountMapper.toBankAccountDTO(campaignBankAccount.get());
        }

        return null;
    }



    private void logError(String methodName, DataAccessException ex) {
        log.error("An error occurred in {}.{} while accessing the database. Details: {}",
                getClass().getSimpleName(),
                methodName,
                ex.getMessage());
    }
}