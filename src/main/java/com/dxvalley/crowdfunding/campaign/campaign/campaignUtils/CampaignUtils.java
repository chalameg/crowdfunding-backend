package com.dxvalley.crowdfunding.campaign.campaign.campaignUtils;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.CampaignBankAccountRepository;
import com.dxvalley.crowdfunding.exception.customException.BadRequestException;
import com.dxvalley.crowdfunding.exception.customException.ForbiddenException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignUtils {
    public final CampaignRepository campaignRepository;
    public final CampaignBankAccountRepository campaignBankAccountRepository;

    public Campaign saveCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public Campaign utilGetCampaignById(Long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no campaign with this ID."));
    }

    public Campaign utilGetCampaignByIdAndStage(Long campaignId, CampaignStage campaignStage) {
        return campaignRepository.findCampaignByIdAndCampaignStage(campaignId, campaignStage)
                .orElseThrow(() -> new ResourceNotFoundException("No pending campaign found with the provided ID."));
    }

    // Checks if the provided campaign data is valid.
    public boolean isValidCampaign(Campaign campaign) {
        return campaign.getShortDescription() != null && !campaign.getShortDescription().isEmpty()
                && campaign.getDescription() != null && !campaign.getDescription().isEmpty()
                && ((campaign.getImages() != null && !campaign.getImages().isEmpty()) || (campaign.getVideos() != null && !campaign.getVideos().isEmpty()))
                && campaign.getGoalAmount() != 0
                && campaign.getProjectType() != null && !campaign.getProjectType().isEmpty()
                && campaign.getCampaignDuration() != 0
                && campaign.getBankAccount() != null;
    }

    public void validateCampaignStage(Campaign campaign, CampaignStage requiredStage, String errorMessage) {
        if (campaign.getCampaignStage() != requiredStage)
            throw new ForbiddenException(errorMessage);
    }

    public void validateCampaignStage(Campaign campaign, List<CampaignStage> requiredStages, String errorMessage) {
        if (!requiredStages.contains(campaign.getCampaignStage())) {
            throw new ForbiddenException(errorMessage);
        }
    }

    public void validateCampaignForSubmission(Campaign campaign) {

        validateCampaignStage(campaign, List.of(CampaignStage.INITIAL, CampaignStage.PENDING),
                "Campaign cannot be submitted for approval unless it is in the initial or pending stage");

        if (!isValidCampaign(campaign))
            throw new BadRequestException("Unable to submit the campaign for approval with the provided data." +
                    " Please provide all required information and try again.");
    }


    // Retrieves Campaigns  by associated bank accountNumber.
    public List<Campaign> getCampaignsByBankAccount(String accountNumber) {
        return campaignRepository.findByBankAccountAccountNumber(accountNumber);
    }
}