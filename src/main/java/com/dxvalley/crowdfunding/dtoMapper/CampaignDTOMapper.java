package com.dxvalley.crowdfunding.dtoMapper;

import com.dxvalley.crowdfunding.dto.CampaignDTO;
import com.dxvalley.crowdfunding.models.Campaign;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CampaignDTOMapper implements Function<Campaign, CampaignDTO> {
    @Override
    public CampaignDTO apply(Campaign campaign) {
        var campaignDTO = new CampaignDTO();

        campaignDTO.setCampaignId(campaign.getCampaignId());
        campaignDTO.setTitle(campaign.getTitle());
        campaignDTO.setShortDescription(campaign.getShortDescription());
        campaignDTO.setCity(campaign.getCity());
        campaignDTO.setImageUrl(campaign.getImageUrl());
        campaignDTO.setGoalAmount(campaign.getGoalAmount());
        campaignDTO.setCampaignStage(campaign.getCampaignStage());
        campaignDTO.setCampaignDuration(campaign.getCampaignDuration());
        campaignDTO.setProjectType(campaign.getProjectType());
        campaignDTO.setNumberOfBackers(campaign.getNumberOfBackers());
        campaignDTO.setNumberOfLikes(campaign.getNumberOfLikes());
        campaignDTO.setTotalAmountCollected(campaign.getTotalAmountCollected() + " is collected out of " + campaign.getGoalAmount());
        if (campaign.getExpiredAt() != null) {
            campaignDTO.setExpiredAt(campaign.getExpiredAt());
            campaignDTO.setCampaignDurationLeft(CampaignDTO.campaignDurationLeft(campaign.getExpiredAt()));
        }

        return campaignDTO;
    }
}
