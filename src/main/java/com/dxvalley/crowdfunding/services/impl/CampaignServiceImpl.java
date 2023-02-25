package com.dxvalley.crowdfunding.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.dxvalley.crowdfunding.services.CampaignSubCategoryService;
import com.dxvalley.crowdfunding.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.dto.CampaignAddRequestDto;
import com.dxvalley.crowdfunding.dto.CampaignDTO;
import com.dxvalley.crowdfunding.dtoMapper.CampaignDTOMapper;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.CampaignStage;
import com.dxvalley.crowdfunding.models.CampaignSubCategory;
import com.dxvalley.crowdfunding.models.FundingType;
import com.dxvalley.crowdfunding.repositories.*;
import com.dxvalley.crowdfunding.services.FundingTypeService;
import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.services.CampaignService;

@Service
public class CampaignServiceImpl implements CampaignService {
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private CampaignBankAccountRepository campaignBankAccountRepository;
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private RewardRepository rewardRepository;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FundingTypeService fundingTypeService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    CampaignSubCategoryService campaignSubCategoryService;
    @Autowired
    UserService userService;
    @Autowired
    private CampaignDTOMapper campaignDTOMapper;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public Campaign addCampaign(CampaignAddRequestDto campaignAddRequestDto) {
        Campaign campaign = new Campaign();
        FundingType fundingType = fundingTypeService.getFundingTypeById(campaignAddRequestDto.getFundingTypeId());
        CampaignSubCategory campaignSubCategory = campaignSubCategoryService
                .getCampaignSubCategoryById(campaignAddRequestDto.getCampaignSubCategoryId());
        var user = userService.getUserByUsername(campaignAddRequestDto.getOwner());

        campaign.setTitle(campaignAddRequestDto.getTitle());
        campaign.setCity(campaignAddRequestDto.getCity());
        campaign.setOwner(user.getUsername());
        campaign.setCampaignSubCategory(campaignSubCategory);
        campaign.setFundingType(fundingType);
        campaign.setCreatedAt(LocalDateTime.now().format(dateTimeFormatter));
        campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
        campaign.setIsEnabled(false);
        campaign.setCampaignStage(CampaignStage.INITIAL);

        return campaignRepository.save(campaign);
    }

    @Override
    public Campaign editCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> getCampaigns() {
        var campaigns = campaignRepository.findAll();
        if (campaigns.size() == 0) {
            throw new ResourceNotFoundException("Currently, There is no campaign.");
        }
        return campaigns;
    }

    @Override
    public List<Campaign> getEnabledCampaigns() {
        var campaigns = campaignRepository.findAllEnabledCampaigns();
        if (campaigns.size() == 0) {
            throw new ResourceNotFoundException("Currently, There is no Enabled campaign.");
        }
        return campaigns;
    }

    @Override
    public Campaign getCampaignById(Long campaignId) {

        Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId).orElseThrow(
                () -> new ResourceNotFoundException("There is no campaign with this ID.")
        );

        var campaignBankAccount = campaignBankAccountRepository.findCampaignBankAccountByCampaignId(campaignId);
        var collaborators = collaboratorRepository.findAllCollaboratorByCampaignId(campaignId);
        var rewards = rewardRepository.findRewardsByCampaignId(campaignId);
        var promotions = promotionRepository.findPromotionByCampaignId(campaignId);
        var user = userRepository.findUserByUsername(campaign.getOwner(), true).get();
        var totalAmountCollected = paymentRepository.findTotalAmountOfPaymentForCampaign(campaignId);
        var contributors = paymentRepository.findPaymentByCampaignId(campaignId);

        campaign.setCampaignBankAccount(campaignBankAccount.get());
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
        var campaigns = campaignRepository.findByCampaignByCategoryId(categoryId);
        if (campaigns.size() == 0) {
            throw new ResourceNotFoundException("There is no campaign for this category.");
        }
        return campaigns;
    }

    @Override
    public List<Campaign> getCampaignBySubCategory(Long subCategoryId) {
        var campaigns = campaignRepository.findByCampaignBySubCategoryId(subCategoryId);
        if (campaigns.size() == 0) {
            throw new ResourceNotFoundException("There is no campaign for this sub-category.");
        }
        return campaigns;
    }

    @Override
    public List<Campaign> getCampaignsByOwner(String owner) {
        var campaigns = campaignRepository.findCampaignsByOwner(owner);
        if (campaigns.size() == 0) {
            throw new ResourceNotFoundException("There is no campaign for this User.");
        }

        return campaigns;
    }

    @Override
    public Campaign enableCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId).orElseThrow(
                () -> new ResourceNotFoundException("There is no campaign with this ID.")
        );

        if (campaign.getIsEnabled()) {
            return campaign;
        }

        campaign.setIsEnabled(true);
        campaign.setCampaignStage(CampaignStage.FUNDING);
        campaign.setEnabledAt(LocalDateTime.now().format(dateTimeFormatter));
        campaign.setExpiredAt(LocalDateTime.now().plusDays(campaign.getCampaignDuration()).format(dateTimeFormatter));

        return campaignRepository.save(campaign);
    }

    @Override
    public List<CampaignDTO> searchCampaigns(String tempSearchParam) {
//        to change string input :tech health food " into sth like "tech|health|food"
        var searchParamArray = tempSearchParam.trim().split("\\W+");
        ;
        var searchParam = searchParamArray[0];
        for (int i = 1; i < searchParamArray.length; i++) {
            searchParam = searchParam + "|" + searchParamArray[i];
        }
        var campaigns = campaignRepository.searchForCampaigns(searchParam).stream()
                .map(campaignDTOMapper).collect(Collectors.toList());
        if (campaigns.size() == 0)
            throw new ResourceNotFoundException("There is no campaign with this search parameter.");
        return campaigns;
    }

    @Override
    public List<Campaign> getCampaignsByFundingType(Long fundingTypeId) {
        var campaigns = campaignRepository.findCampaignsByFundingType(fundingTypeId);
        if (campaigns.size() == 0)
            throw new ResourceNotFoundException("There is no campaign for this Funding Type.");
        return campaigns;
    }

    @Override
    public List<Campaign> getCampaignsByStage(String campaignStage) {
        CampaignStage result = CampaignStage.lookup(campaignStage);
        var campaigns = campaignRepository.findCampaignsByCampaignStage(result);
        if (campaigns.size() == 0)
            throw new ResourceNotFoundException("There is no campaign at " + campaignStage + " stage.");
        return campaigns;
    }

    @Override
    public void deleteCampaign(Long campaignId) {
        var campaign = getCampaignById(campaignId);
        if (campaign.getCampaignBankAccount() != null)
            campaignBankAccountRepository.delete(campaign.getCampaignBankAccount());
        if (campaign.getRewards() != null && campaign.getRewards().size() != 0)
            rewardRepository.deleteAll(campaign.getRewards());
        if (campaign.getCollaborators() != null && campaign.getCollaborators().size() != 0)
            collaboratorRepository.deleteAll(campaign.getCollaborators());
        if (campaign.getPromotions() != null && campaign.getPromotions().size() != 0)
            promotionRepository.deleteAll(campaign.getPromotions());
        if (campaign.getContributors() != null && campaign.getContributors().size() != 0)
            paymentRepository.deleteAll(campaign.getContributors());
        campaignRepository.deleteById(campaignId);
    }

}




















