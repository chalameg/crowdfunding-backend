package com.dxvalley.crowdfunding.dtoMapper;

import com.dxvalley.crowdfunding.dto.CampaignDTO;
import com.dxvalley.crowdfunding.models.Campaign;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CampaignDTOMapper implements Function<Campaign, CampaignDTO> {
    @Override
    public CampaignDTO apply(Campaign campaign) {
        return new CampaignDTO(
                campaign.getCampaignId(),
                campaign.getTitle(),
                campaign.getShortDescription(),
                campaign.getCity(),
                campaign.getImageUrl(),
                campaign.getGoalAmount(),
                campaign.getCampaignDuration(),
                campaign.getProjectType(),
                campaign.getCampaignStage()
        );
    }
}
