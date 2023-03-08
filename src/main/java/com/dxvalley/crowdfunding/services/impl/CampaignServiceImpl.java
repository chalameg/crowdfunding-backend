package com.dxvalley.crowdfunding.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.dxvalley.crowdfunding.dto.CampaignDTO;
import com.dxvalley.crowdfunding.dto.CampaignLikeDTO;
import com.dxvalley.crowdfunding.exceptions.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.models.*;
import com.dxvalley.crowdfunding.services.CampaignSubCategoryService;
import com.dxvalley.crowdfunding.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.dto.CampaignAddRequestDto;
import com.dxvalley.crowdfunding.dtoMapper.CampaignDTOMapper;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.repositories.*;
import com.dxvalley.crowdfunding.services.FundingTypeService;
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
    private FundingTypeService fundingTypeService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    CampaignSubCategoryService campaignSubCategoryService;
    @Autowired
    UserService userService;
    @Autowired
    private CampaignDTOMapper campaignDTOMapper;
    @Autowired
    private CampaignLikeRepository campaignLikeRepository;
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
        campaign.setGoalAmount((double) 0);
        campaign.setTotalAmountCollected((double) 0);
        campaign.setNumberOfBackers(0);
        campaign.setNumberOfLikes(0);
        campaign.setCampaignDuration((short) 0);

        return campaignRepository.save(campaign);
    }

    @Override
    public String likeCampaign(CampaignLikeDTO campaignLikeDTO) {
        var campaignLike = new CampaignLike();
        var result = campaignLikeRepository.findByCampaignCampaignIdAndUserUserId(
                campaignLikeDTO.getCampaignId(), campaignLikeDTO.getUserId());
        var campaign = this.getCampaignById(campaignLikeDTO.getCampaignId());
        if (result != null) {
            campaignLikeRepository.delete(result);
            campaign.setNumberOfLikes(campaign.getNumberOfLikes() - 1);
            campaignRepository.save(campaign);
            return "Disliked Successfully";
        }
        campaignLike.setUser(userService.getUserById(campaignLikeDTO.getUserId()));
        campaignLike.setCampaign(campaign);
        campaignLikeRepository.save(campaignLike);
        campaign.setNumberOfLikes(campaign.getNumberOfLikes() + 1);
        campaignRepository.save(campaign);
        return "Liked Successfully";
    }

    @Override
    public Campaign editCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    @Override
    public List<CampaignDTO> getCampaigns() {
        var campaigns = campaignRepository.findCampaignsByCampaignStageIn(List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));
        if (campaigns.size() == 0) {
            throw new ResourceNotFoundException("Currently, There is no campaign.");
        }
        return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
    }

    @Override
    public List<CampaignDTO> getEnabledCampaigns() {
        var campaigns = campaignRepository.findCampaignsByIsEnabled(true);
        if (campaigns.size() == 0) {
            throw new ResourceNotFoundException("Currently, There is no Enabled campaign.");
        }
        return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
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
        var user = userService.getUserByUsername(campaign.getOwner());
        var contributors = paymentRepository.findPaymentsByCampaignCampaignId(campaignId);

        if (campaignBankAccount.isPresent())
            campaign.setCampaignBankAccount(campaignBankAccount.get());
        campaign.setCollaborators(collaborators);
        campaign.setRewards(rewards);
        campaign.setPromotions(promotions);
        campaign.setContributors(contributors);
        campaign.setOwnerFullName(user.getFullName());
        campaign.setNumberOfBackers(contributors.size());
        return campaign;
    }

    @Override
    public List<CampaignDTO> getCampaignByCategory(Long categoryId) {
        var campaigns = campaignRepository.
                findCampaignsByCampaignSubCategoryCampaignCategoryCampaignCategoryIdAndCampaignStageIn(
                        categoryId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));
        if (campaigns.size() == 0) {
            throw new ResourceNotFoundException("There is no campaign for this category.");
        }
        return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
    }

    @Override
    public List<CampaignDTO> getCampaignBySubCategory(Long subCategoryId) {
        var campaigns = campaignRepository.
                findCampaignsByCampaignSubCategoryCampaignSubCategoryIdAndCampaignStageIn(
                        subCategoryId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));
        if (campaigns.size() == 0) {
            throw new ResourceNotFoundException("There is no campaign for this sub-category.");
        }
        return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
    }

    @Override
    public List<CampaignDTO> getCampaignsByOwner(String owner) {
        var campaigns = campaignRepository.findCampaignsByOwner(owner);
        if (campaigns.size() == 0) {
            throw new ResourceNotFoundException("There is no campaign for this User.");
        }
        return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
    }

    @Override
    public Campaign enableCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId).orElseThrow(
                () -> new ResourceNotFoundException("There is no campaign with this ID.")
        );
        if (campaign.getIsEnabled()) throw new ResourceAlreadyExistsException("This campaign is already enabled.");
        campaign.setIsEnabled(true);
        campaign.setCampaignStage(CampaignStage.FUNDING);
        campaign.setEnabledAt(LocalDateTime.now().format(dateTimeFormatter));
        campaign.setExpiredAt(LocalDateTime.now().plusDays(campaign.getCampaignDuration()).format(dateTimeFormatter));
        return campaignRepository.save(campaign);
    }

    @Override
    public List<CampaignDTO> searchCampaigns(String searchParam) {
        // Clean up the search parameter string by trimming whitespace and splitting by non-word characters
        String[] searchParamArray = searchParam.trim().split("\\W+");

        // Join the cleaned-up search parameters with "|" to create a regex search pattern
        String searchPattern = String.join("|", searchParamArray);

        // Use the search pattern to query the repository and map the results to CampaignDTO objects
        List<CampaignDTO> campaigns = campaignRepository.searchForCampaigns(searchPattern)
                .stream()
                .map(campaignDTOMapper)
                .collect(Collectors.toList());

        if (campaigns.isEmpty()) {
            throw new ResourceNotFoundException("No campaigns found with this search parameter.");
        }

        return campaigns;
    }


    @Override
    public List<CampaignDTO> getCampaignsByFundingType(Long fundingTypeId) {
        var campaigns = campaignRepository.findCampaignsByFundingTypeFundingTypeIdAndCampaignStageIn(
                fundingTypeId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));
        if (campaigns.size() == 0)
            throw new ResourceNotFoundException("There is no campaign for this Funding Type.");

        return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
    }

    @Override
    public List<CampaignDTO> getCampaignsByStage(String campaignStage) {
        CampaignStage result = CampaignStage.lookup(campaignStage);
        var campaigns = campaignRepository.findCampaignsByCampaignStage(result);
        if (campaigns.size() == 0)
            throw new ResourceNotFoundException("There is no campaign at " + campaignStage + " stage.");

        return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
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




















