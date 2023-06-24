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
        return (Campaign)this.campaignRepository.save(campaign);
    }

    public Campaign utilGetCampaignById(Long campaignId) {
        return (Campaign)this.campaignRepository.findById(campaignId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no campaign with this ID.");
        });
    }

    public Campaign utilGetCampaignByIdAndStage(Long campaignId, CampaignStage campaignStage) {
        return (Campaign)this.campaignRepository.findCampaignByIdAndCampaignStage(campaignId, campaignStage).orElseThrow(() -> {
            return new ResourceNotFoundException("No pending campaign found with the provided ID.");
        });
    }

    public boolean isValidCampaign(Campaign campaign) {
        return campaign.getShortDescription() != null && !campaign.getShortDescription().isEmpty() && campaign.getDescription() != null && !campaign.getDescription().isEmpty() && (campaign.getImages() != null && !campaign.getImages().isEmpty() || campaign.getVideo() != null) && campaign.getGoalAmount() != 0.0 && campaign.getProjectType() != null && !campaign.getProjectType().isEmpty() && campaign.getCampaignDuration() != 0 && campaign.getBankAccount() != null;
    }

    public void validateCampaignStage(Campaign campaign, CampaignStage requiredStage, String errorMessage) {
        if (campaign.getCampaignStage() != requiredStage) {
            throw new ForbiddenException(errorMessage);
        }
    }

    public void validateCampaignStage(Campaign campaign, List<CampaignStage> requiredStages, String errorMessage) {
        if (!requiredStages.contains(campaign.getCampaignStage())) {
            throw new ForbiddenException(errorMessage);
        }
    }

    public void validateCampaignForSubmission(Campaign campaign) {
        this.validateCampaignStage(campaign, List.of(CampaignStage.INITIAL, CampaignStage.PENDING), "Campaign cannot be submitted for approval unless it is in the initial or pending stage");
        if (!this.isValidCampaign(campaign)) {
            throw new BadRequestException("Unable to submit the campaign for approval with the provided data. Please provide all required information and try again.");
        }
    }

    public List<Campaign> getCampaignsByBankAccount(String accountNumber) {
        return this.campaignRepository.findByBankAccountAccountNumber(accountNumber);
    }
}