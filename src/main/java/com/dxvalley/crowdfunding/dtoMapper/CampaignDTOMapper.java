package com.dxvalley.crowdfunding.dtoMapper;

import com.dxvalley.crowdfunding.dto.CampaignDTO;
import com.dxvalley.crowdfunding.models.Campaign;
import org.springframework.stereotype.Service;
import java.text.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
        campaignDTO.setTotalAmountCollected(campaign.getGoalAmount());
        if (campaign.getExpiredAt() != null) {
            campaignDTO.setExpiredAt(campaign.getExpiredAt());
            campaignDTO.setCampaignDurationLeft(CampaignDTO.campaignDurationLeft(campaign.getExpiredAt()));
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Duration duration = Duration.between(LocalDateTime.now(), LocalDateTime.parse(campaign.getCreatedAt(), dateTimeFormatter));

        campaignDTO.setDaysLeft((int)(campaign.getCampaignDuration() - duration.toDays()));

        return campaignDTO;
    }
}
