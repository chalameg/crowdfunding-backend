package com.dxvalley.crowdfunding.campaign.campaign.dto;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CampaignDTOMapper implements Function<Campaign, CampaignDTO> {
    private final DateTimeFormatter dateTimeFormatter;

    @Override
    public CampaignDTO apply(Campaign campaign) {
        CampaignDTO campaignDTO = new CampaignDTO();

        campaignDTO.setCampaignId(campaign.getId());
        campaignDTO.setTitle(campaign.getTitle());
        campaignDTO.setShortDescription(campaign.getShortDescription());

        campaignDTO.setCity(campaign.getCity());
        campaignDTO.setImages(campaign.getImages());
        campaignDTO.setVideos(campaign.getVideos());
        campaignDTO.setGoalAmount(campaign.getGoalAmount());

        campaignDTO.setTotalAmountCollected(campaign.getTotalAmountCollected());
        campaignDTO.setCampaignStage(campaign.getCampaignStage());
        campaignDTO.setProjectType(campaign.getProjectType());

        campaignDTO.setNumberOfBackers(campaign.getNumberOfBackers());
        campaignDTO.setNumberOfLikes(campaign.getNumberOfLikes());
        campaignDTO.setCampaignDuration(campaign.getCampaignDuration());

        campaignDTO.setCreatedAt(campaign.getCreatedAt());
        campaignDTO.setEnabledAt(campaign.getApprovedAt());
        campaignDTO.setCompletedAt(campaign.getCompletedAt());

        if (campaign.getCompletedAt() != null)
            campaignDTO.setCampaignDurationLeft(campaignDurationLeft(campaign.getCompletedAt()));

        campaignDTO.setPercentageCollected(percentageCollected(
                campaign.getTotalAmountCollected(),
                campaign.getGoalAmount()));

        return campaignDTO;
    }

    public CampaignDTO applyById(Campaign campaign) {
        CampaignDTO campaignDTO = apply(campaign);

        campaignDTO.setDescription(campaign.getDescription());
        campaignDTO.setRisks(campaign.getRisks());
        campaignDTO.setVideos(campaign.getVideos());
        campaignDTO.setOwner(campaign.getUser().getUsername());
        campaignDTO.setCampaignDuration(campaign.getCampaignDuration());
        campaignDTO.setFundingType(campaign.getFundingType());
        campaignDTO.setCampaignSubCategory(campaign.getCampaignSubCategory());

        return campaignDTO;
    }

    private String campaignDurationLeft(String expiredAt) {
        LocalDateTime expirationDateTime = LocalDateTime.parse(expiredAt, dateTimeFormatter);

        if (expirationDateTime.isBefore(LocalDateTime.now()))
            return "COMPLETED";
        else {
            Period period = Period.between(LocalDate.now(), expirationDateTime.toLocalDate());
            if (period.getDays() > 1) {
                return period.getDays() + " DAYS LEFT";
            } else {
                return "ON THE LAST DAY";
            }
        }
    }

    private double percentageCollected(double totalAmountCollected, double goalAmount) {
        if (goalAmount == 0)
            return 0.0;

        double percentage = (totalAmountCollected / goalAmount) * 100;
        return Math.round(percentage * 100.0) / 100.0; // Round to two decimal places
    }
}
