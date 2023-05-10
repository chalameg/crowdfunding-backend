package com.dxvalley.crowdfunding.campaign.campaign.dto;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CampaignDTOMapper implements Function<Campaign, CampaignDTO> {
    private final DateTimeFormatter dateTimeFormatter;

    @Override
    public CampaignDTO apply(Campaign campaign) {
        var campaignDTO = new CampaignDTO();

        campaignDTO.setCampaignId(campaign.getCampaignId());
        campaignDTO.setTitle(campaign.getTitle());
        campaignDTO.setShortDescription(campaign.getShortDescription());

        campaignDTO.setCity(campaign.getCity());
        campaignDTO.setImageUrl(campaign.getImageUrl());
        campaignDTO.setVideoUrl(campaign.getVideoLink());
        campaignDTO.setFiles(campaign.getFiles());
        campaignDTO.setGoalAmount(campaign.getGoalAmount());

        campaignDTO.setTotalAmountCollected(campaign.getTotalAmountCollected());
        campaignDTO.setCampaignStage(campaign.getCampaignStage());
        campaignDTO.setProjectType(campaign.getProjectType());

        campaignDTO.setNumberOfBackers(campaign.getNumberOfBackers());
        campaignDTO.setNumberOfLikes(campaign.getNumberOfLikes());
        campaignDTO.setCampaignDuration(campaign.getCampaignDuration());

        campaignDTO.setCreatedAt(campaign.getCreatedAt());
        campaignDTO.setEnabledAt(campaign.getEnabledAt());
        campaignDTO.setCompletedAt(campaign.getExpiredAt());

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
        if (duration.toDays() < 0L)
            return -1;
        return (short) duration.toDays();
    }

    private double percentageCollected(Double totalAmountCollected, Double goalAmount, String name) {
        if (goalAmount == 0)
            return 0;
        return (totalAmountCollected / goalAmount) * 100;
    }
}
