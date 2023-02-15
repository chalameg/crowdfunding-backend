package com.dxvalley.crowdfunding.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.dxvalley.crowdfunding.dto.CampaignAddRequestDto;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.CampaignStage;
import com.dxvalley.crowdfunding.models.CampaignSubCategory;
import com.dxvalley.crowdfunding.models.FundingType;
import com.dxvalley.crowdfunding.repositories.*;
import com.dxvalley.crowdfunding.services.CampaignSubCategoryService;
import com.dxvalley.crowdfunding.services.FundingTypeService;
import com.dxvalley.crowdfunding.services.PaymentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.services.CampaignService;

@Service
public class CampaignServiceImpl implements CampaignService {
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private CampaignBankAccountRepository campaignBankAccountRepository;
    @Autowired private CollaboratorRepository collaboratorRepository;
    @Autowired
    private RewardRepository rewardRepository;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FundingTypeService fundingTypeService;
    @Autowired
    private CampaignSubCategoryService campaignSubCategoryService;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private PaymentInfoRepository paymentInfoRepository;


    @Override
    public Campaign addCampaign(CampaignAddRequestDto campaignAddRequestDto) {
        Campaign campaign = new Campaign();
            FundingType fundingType = fundingTypeService.getFundingTypeById(campaignAddRequestDto.getFundingTypeId());
            CampaignSubCategory campaignSubCategory =  campaignSubCategoryService
                    .getCampaignSubCategoryById(campaignAddRequestDto.getCampaignSubCategoryId());

        LocalDateTime createdAt = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        campaign.setTitle(campaignAddRequestDto.getTitle());
        campaign.setCity(campaignAddRequestDto.getCity());
        campaign.setOwner(campaignAddRequestDto.getOwner());
        campaign.setCampaignSubCategory(campaignSubCategory);
        campaign.setFundingType(fundingType);
        campaign.setCreatedAt(createdAt.format(dateTimeFormatter));
        campaign.setEditedAt(createdAt.format(dateTimeFormatter));
        campaign.setIsEnabled(false);
        campaign.setCampaignStage(CampaignStage.INITIAL);

        return this.campaignRepository.save(campaign);
    }

    @Override
    public Campaign editCampaign(Campaign campaign) {
        return this.campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> getCampaigns() {
        var campaigns = campaignRepository.findAll();
        if(campaigns.size() == 0){
            throw new ResourceNotFoundException("Currently, There is no campaign.");
        }
        return campaigns;
    }
    @Override
    public List<Campaign> getEnabledCampaigns() {
        var campaigns = campaignRepository.findAllCampaigns();
        if(campaigns.size() == 0){
            throw new ResourceNotFoundException("Currently, There is no active campaign.");
        }
        return campaigns;
    }

    @Override
    public Campaign getCampaignById(Long campaignId) throws ResourceNotFoundException {

        Campaign campaign =campaignRepository.findCampaignByCampaignId(campaignId).orElseThrow(
                () -> new ResourceNotFoundException("There is no campaign with this ID.")
        );

        var campaignBankAccount =  campaignBankAccountRepository.findCampaignBankAccountByCampaignId(campaignId);
        var collaborators = collaboratorRepository.findAllCollaboratorByCampaignId(campaignId);
        var rewards = rewardRepository.findRewardsByCampaignId(campaignId);
        var promotions = promotionRepository.findPromotionByCampaignId(campaignId);
        var user = userRepository.findUserByUsername(campaign.getOwner(),true).get();
        var totalAmountCollected = paymentInfoRepository.findTotalAmountOfPaymentForCampaign(campaignId);
        var contributors = paymentInfoRepository.findPaymentInfoByCampaignId(campaignId);

        campaign.setCampaignBankAccount(campaignBankAccount);
        campaign.setCollaborators(collaborators);
        campaign.setRewards(rewards);
        campaign.setPromotions(promotions);
        campaign.setContributors(contributors);
        campaign.setOwnerFullName(user.getFullName());
        campaign.setTotalAmountCollected(totalAmountCollected + " is collected out of " + campaign.getGoalAmount());
        campaign.setNumberOfBackers(contributors.size());
        return campaign;
    }

    @Override
    public List<Campaign> getCampaignByCategory(Long categoryId) {
        var campaigns =  campaignRepository.findByCampaignByCategoryId(categoryId);
        if(campaigns.size() == 0){
            throw new ResourceNotFoundException("There is no campaign for this category.");
        }
        return campaigns;
    }

    @Override
    public List<Campaign> getCampaignBySubCategory(Long subCategoryId) {
        var campaigns =  campaignRepository.findByCampaignBySubCategoryId(subCategoryId);
        if(campaigns.size() == 0){
            throw new ResourceNotFoundException("There is no campaign for this sub-category.");
        }
        return campaigns;
    }
    @Override
    public List<Campaign> getCampaignsByOwner(String owner) {
        var campaigns = campaignRepository.findCampaignsByOwner(owner);
        if(campaigns.size() == 0){
            throw new ResourceNotFoundException("There is no campaign for this User.");
        }

        return campaigns;
    }

    @Override
    public Campaign enableCampaign(Long campaignId) {
        Campaign campaign =campaignRepository.findCampaignByCampaignId(campaignId).orElseThrow(
                () -> new ResourceNotFoundException("There is no campaign with this ID.")
        );

        if(campaign.getIsEnabled()){
            return campaign;
        }
        LocalDateTime enabledAt = LocalDateTime.now();
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(campaign.getCampaignDuration());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        campaign.setIsEnabled(true);
        campaign.setCampaignStage(CampaignStage.FUNDING);
        campaign.setEnabledAt(enabledAt.format(dateTimeFormatter));
        campaign.setExpiredAt(expiredAt.format(dateTimeFormatter));

        return campaignRepository.save(campaign);
    }

    @Override
    public String deleteCampaign(Long campaignId) {
        var campaign = campaignRepository.findCampaignByCampaignId(campaignId).orElseThrow(
                () -> new ResourceNotFoundException("There is no campaign with this ID.")
        );
        campaignRepository.deleteById(campaignId);
        return "Campaign successfully deleted";
    }

}
