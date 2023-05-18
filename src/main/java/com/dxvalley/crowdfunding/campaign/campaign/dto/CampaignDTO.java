package com.dxvalley.crowdfunding.campaign.campaign.dto;

import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountDTO;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.Collaborator;
import com.dxvalley.crowdfunding.campaign.campaignFundingType.FundingType;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.Promotion;
import com.dxvalley.crowdfunding.campaign.campaignReward.Reward;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.CampaignSubCategory;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentResponse;
import lombok.Data;

import java.util.List;

@Data
public class CampaignDTO {
    private Long campaignId;
    private String title;
    private String shortDescription;
    private String city;
    private String imageUrl;
    private String videoUrl;
    private List<String> files;
    private String projectType;
    private CampaignStage campaignStage;
    private Double goalAmount;
    private String owner;
    private String description;
    private String risks;
    private FundingType fundingType;
    private CampaignSubCategory campaignSubCategory;
    private Double commissionRate;
    private Double totalAmountCollected;
    private Double percentageCollected;
    private Integer numberOfLikes;
    private Integer numberOfBackers;
    private Short campaignDuration;
    private Short campaignDurationLeft;
    private String createdAt;
    private String enabledAt;
    private String completedAt;
    private String ownerFullName;
    private BankAccountDTO campaignBankAccount;
    private Integer numberOfCampaigns;
    private List<PaymentResponse> contributors;
    private List<Reward> rewards;
    private List<Promotion> promotions;
    private List<Collaborator> collaborators;

}
