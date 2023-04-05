package com.dxvalley.crowdfunding.dto;

import com.dxvalley.crowdfunding.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CampaignDTO {
    private Long campaignId;
    private String title;
    private String shortDescription;
    private String city;
    private String imageUrl;
    private String videoUrl;
    private String projectType;
    private CampaignStage campaignStage;
    private Double goalAmount;
    private Double commissionRate;
    private Double totalAmountCollected;
    private Double percentageCollected;
    private Integer numberOfLikes;
    private Integer numberOfBackers;
    private Short campaignDurationLeft;
    private String ownerFullName;
    private CampaignBankAccount campaignBankAccount;
    private Integer numberOfCampaigns;
    private String owner;
    private String description;
    private String risks;
    private Short campaignDuration;
    private FundingType fundingType;
    private CampaignSubCategory campaignSubCategory;
    private List<Payment> contributors;
    private List<Reward> rewards;
    private List<Promotion> promotions;
    private List<Collaborator> collaborators;

}
