package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignMapper;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.CollaboratorService;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaboratorResponse;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.PromotionService;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionResponse;
import com.dxvalley.crowdfunding.campaign.campaignReward.RewardService;
import com.dxvalley.crowdfunding.campaign.campaignReward.dto.RewardResponse;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.payment.PaymentService;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CampaignRetrievalServiceImpl implements CampaignRetrievalService {
    private final CampaignRepository campaignRepository;
    private final CollaboratorService collaboratorService;
    private final RewardService rewardService;
    private final PromotionService promotionService;
    private final PaymentService paymentService;
    private final CampaignMapper campaignMapper;
    private final CampaignUtils campaignUtils;


    // Retrieves the list of campaigns in the funding or completed stage.
    @Override
    public List<CampaignDTO> getCampaigns() {
        List<Campaign> campaigns = campaignRepository.
                findCampaignsByCampaignStageIn(List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));

        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("Currently, there are no campaigns available.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

    // Retrieves campaigns by campaign stage.
    @Override
    public List<CampaignDTO> getCampaignsByStage(String campaignStage) {

        CampaignStage result = CampaignStage.lookup(campaignStage);
        List<Campaign> campaigns = campaignRepository.findCampaignsByCampaignStage(result);

        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("There are no campaigns available at the " + campaignStage + " stage.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

    // Retrieves a campaign by its ID along with additional details such as bank account, collaborators, rewards, promotions, contributors, and owner information.
    @Override
    public CampaignDTO getCampaignById(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        CampaignDTO campaignDTO = campaignMapper.toDTOById(campaign);

        // Retrieve and Set additional details in the campaign DTO
        setAdditionalDetails(campaignDTO, campaignId);

        return campaignDTO;
    }

    private void setAdditionalDetails(CampaignDTO campaignDTO, Long campaignId) {
        List<CollaboratorResponse> collaborators = collaboratorService.getCollaboratorByCampaignId(campaignId);
        List<RewardResponse> rewards = rewardService.getByCampaign(campaignId);
        List<PromotionResponse> promotions = promotionService.getPromotionByCampaign(campaignId);
        List<PaymentResponse> contributors = paymentService.getPaymentByCampaignId(campaignId);

        campaignDTO.setCollaborators(collaborators);
        campaignDTO.setRewards(rewards);
        campaignDTO.setPromotions(promotions);
        campaignDTO.setContributors(contributors);
        campaignDTO.setNumberOfBackers(contributors.size());
    }


    // Retrieves campaigns by category ID in the funding or completed stage.
    @Override
    public List<CampaignDTO> getCampaignByCategory(Long categoryId) {

        List<Campaign> campaigns = campaignRepository.
                findCampaignsByCampaignSubCategoryCampaignCategoryIdAndCampaignStageIn(
                        categoryId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));

        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("There are no campaigns available for this category.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }


    // Retrieves campaigns by sub-category ID in the funding or completed stage.
    @Override
    public List<CampaignDTO> getCampaignBySubCategory(Long subCategoryId) {

        List<Campaign> campaigns = campaignRepository.findCampaignsByCampaignSubCategoryIdAndCampaignStageIn(subCategoryId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));

        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("There are no campaigns available for this sub-category.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }


    // Retrieves campaigns by owner username.
    @Override
    public List<CampaignDTO> getCampaignsByOwner(String username) {

        List<Campaign> campaigns = campaignRepository.findCampaignsByUserUsername(username);

        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("There are no campaigns available for this user.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

    // Searches campaigns based on the provided search parameter.
    @Override
    public List<CampaignDTO> searchCampaigns(String searchParam) {

        // Clean up the search parameter string by trimming whitespace and splitting by non-word characters
        String[] searchParamArray = searchParam.trim().split("\\W+");
        // Join the cleaned-up search parameters with "|" to create a regex search pattern
        String searchPattern = String.join("|", searchParamArray);

        List<Campaign> campaigns = campaignRepository.searchForCampaigns(searchPattern);

        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("No campaigns found with this search parameter.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

    // Retrieves campaigns by funding type ID.
    @Override
    public List<CampaignDTO> getCampaignsByFundingType(Long fundingTypeId) {

        List<Campaign> campaigns = campaignRepository.
                findCampaignsByFundingTypeIdAndCampaignStage(fundingTypeId, CampaignStage.FUNDING);

        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("There are no campaigns available for this funding type.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

}