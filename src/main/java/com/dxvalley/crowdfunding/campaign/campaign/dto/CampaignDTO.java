package com.dxvalley.crowdfunding.campaign.campaign.dto;

import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.file.CampaignFile;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image.CampaignImage;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.CampaignBankAccount;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaboratorResponse;
import com.dxvalley.crowdfunding.campaign.campaignFundingType.FundingType;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionResponse;
import com.dxvalley.crowdfunding.campaign.campaignReward.dto.RewardResponse;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.CampaignSubCategory;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@Data
public class CampaignDTO {
    private Long campaignId;
    private String title;
    private String shortDescription;
    private String city;
    private String projectType;
    private CampaignStage campaignStage;
    private Double goalAmount;
    private String owner;
    private String ownerEmail;
    private String description;
    private String risks;
    private Double commissionRate;
    private Double totalAmountCollected;
    private Double percentageCollected;
    private Integer numberOfLikes;
    private Integer numberOfBackers;
    private Short campaignDuration;
    private String campaignDurationLeft;
    private String createdAt;
    private String enabledAt;
    private String completedAt;
    private Integer numberOfCampaigns;
    private FundingType fundingType;
    private String videoLink;
    private List<CampaignImage> images;
    private List<CampaignFile> files;
    private CampaignBankAccount campaignBankAccount;
    private CampaignSubCategory campaignSubCategory;
    private List<PaymentResponse> contributors;
    private List<RewardResponse> rewards;
    private List<PromotionResponse> promotions;
    private List<CollaboratorResponse> collaborators;
}
