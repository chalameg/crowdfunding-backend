package com.dxvalley.crowdfunding.dto.mapper;

import com.dxvalley.crowdfunding.dto.CampaignDTO;
import com.dxvalley.crowdfunding.model.Campaign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
public class CampaignDTOMapper implements Function<Campaign, CampaignDTO> {
    @Autowired
    private DateTimeFormatter dateTimeFormatter;

    @Override
    public CampaignDTO apply(Campaign campaign) {
        var campaignDTO = new CampaignDTO();

        campaignDTO.setCampaignId(campaign.getCampaignId());
        campaignDTO.setTitle(campaign.getTitle());
        campaignDTO.setShortDescription(campaign.getShortDescription());

        campaignDTO.setCity(campaign.getCity());
        campaignDTO.setImageUrl(campaign.getImageUrl());
        campaignDTO.setVideoUrl(campaign.getVideoLink());
        campaignDTO.setGoalAmount(campaign.getGoalAmount());

        campaignDTO.setTotalAmountCollected(campaign.getTotalAmountCollected());
        campaignDTO.setCampaignStage(campaign.getCampaignStage());
        campaignDTO.setProjectType(campaign.getProjectType());

        campaignDTO.setNumberOfBackers(campaign.getNumberOfBackers());
        campaignDTO.setNumberOfLikes(campaign.getNumberOfLikes());

        if (campaign.getExpiredAt() != null)
            campaignDTO.setCampaignDurationLeft(campaignDurationLeft(campaign.getExpiredAt()));

        campaignDTO.setPercentageCollected(percentageCollected(
                campaign.getTotalAmountCollected(),
                campaign.getGoalAmount(),
                campaign.getTitle()));

        return campaignDTO;
    }


    public CampaignDTO applyById(Campaign campaign) {
        CampaignDTO campaignDTO = apply(campaign);

        campaignDTO.setDescription(campaign.getDescription());
        campaignDTO.setRisks(campaign.getRisks());
        campaignDTO.setVideoUrl(campaign.getVideoLink());
        campaignDTO.setOwner(campaign.getOwner());
        campaignDTO.setCampaignDuration(campaign.getCampaignDuration());
        campaignDTO.setFundingType(campaign.getFundingType());
        campaignDTO.setFundingType(campaign.getFundingType());
        campaignDTO.setCampaignSubCategory(campaign.getCampaignSubCategory());

        return campaignDTO;
    }

    private short campaignDurationLeft(String expiredAt) {
        Duration duration = Duration.between(LocalDateTime.now(), LocalDateTime.parse(expiredAt, dateTimeFormatter));
        if (duration.toDays() < 0)
            return -1;
        return (short) duration.toDays();
    }

    private double percentageCollected(Double totalAmountCollected, Double goalAmount, String name) {
        if (goalAmount == 0)
            return 0;
        return (totalAmountCollected / goalAmount) * 100;
    }
}
