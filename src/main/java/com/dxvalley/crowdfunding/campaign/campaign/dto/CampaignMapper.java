package com.dxvalley.crowdfunding.campaign.campaign.dto;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class CampaignMapper {
    private final DateTimeFormatter dateTimeFormatter;

    public CampaignDTO toDTO(Campaign campaign) {
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setCampaignId(campaign.getId());
        campaignDTO.setTitle(campaign.getTitle());
        campaignDTO.setShortDescription(campaign.getShortDescription());
        campaignDTO.setDescription(campaign.getDescription());
        campaignDTO.setCity(campaign.getCity());
        campaignDTO.setImages(campaign.getImages());
        if (campaign.getVideo() != null) {
            campaignDTO.setVideoLink(campaign.getVideo().getVideoUrl());
        }

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
        if (campaign.getCompletedAt() != null) {
            campaignDTO.setCampaignDurationLeft(this.campaignDurationLeft(campaign.getCompletedAt()));
        }

        campaignDTO.setPercentageCollected(this.percentageCollected(campaign.getTotalAmountCollected(), campaign.getGoalAmount()));
        return campaignDTO;
    }

    public CampaignDTO toDTOById(Campaign campaign) {
        CampaignDTO campaignDTO = this.toDTO(campaign);
        campaignDTO.setRisks(campaign.getRisks());
        campaignDTO.setFiles(campaign.getFiles());
        campaignDTO.setOwner(campaign.getUser().getFullName());
        campaignDTO.setOwnerEmail(campaign.getUser().getEmail());
        campaignDTO.setCampaignDuration(campaign.getCampaignDuration());
        campaignDTO.setFundingType(campaign.getFundingType());
        campaignDTO.setCampaignSubCategory(campaign.getCampaignSubCategory());
        if (campaign.getBankAccount() != null) {
            campaignDTO.setCampaignBankAccount(campaign.getBankAccount());
        }

        return campaignDTO;
    }

    private String campaignDurationLeft(String expiredAt) {
        LocalDateTime expirationDateTime = LocalDateTime.parse(expiredAt, this.dateTimeFormatter);
        if (expirationDateTime.isBefore(LocalDateTime.now())) {
            return "COMPLETED";
        } else {
            Period period = Period.between(LocalDate.now(), expirationDateTime.toLocalDate());
            return period.getDays() > 1 ? period.getDays() + " DAYS LEFT" : "ON THE LAST DAY";
        }
    }

    private double percentageCollected(double totalAmountCollected, double goalAmount) {
        if (goalAmount == 0.0) {
            return 0.0;
        } else {
            double percentage = totalAmountCollected / goalAmount * 100.0;
            return (double)Math.round(percentage * 100.0) / 100.0;
        }
    }

}
