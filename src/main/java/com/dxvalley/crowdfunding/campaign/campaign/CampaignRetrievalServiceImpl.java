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
import com.dxvalley.crowdfunding.paymentManager.payment.PaymentRetrievalService;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentResponse;
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
    private final PaymentRetrievalService paymentRetrievalService;
    private final CampaignMapper campaignMapper;
    private final CampaignUtils campaignUtils;

    public List<CampaignDTO> getCampaigns() {
        List<Campaign> campaigns = this.campaignRepository.findCampaignsByCampaignStageIn(List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));
        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("Currently, there are no campaigns available.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();

    }

    public List<CampaignDTO> getCampaignsByStage(String campaignStage) {
        CampaignStage result = CampaignStage.lookup(campaignStage);
        List<Campaign> campaigns = this.campaignRepository.findCampaignsByCampaignStage(result);
        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("There are no campaigns available at the " + campaignStage + " stage.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

    public CampaignDTO getCampaignById(Long campaignId) {
        Campaign campaign = this.campaignUtils.utilGetCampaignById(campaignId);
        CampaignDTO campaignDTO = this.campaignMapper.toDTOById(campaign);
        this.setAdditionalDetails(campaignDTO, campaignId);
        return campaignDTO;
    }

    private void setAdditionalDetails(CampaignDTO campaignDTO, Long campaignId) {
        List<CollaboratorResponse> collaborators = this.collaboratorService.getCollaboratorByCampaignId(campaignId);
        List<RewardResponse> rewards = this.rewardService.getByCampaign(campaignId);
        List<PromotionResponse> promotions = this.promotionService.getPromotionByCampaign(campaignId);
        List<PaymentResponse> contributors = this.paymentRetrievalService.getPaymentByCampaignId(campaignId);
        campaignDTO.setCollaborators(collaborators);
        campaignDTO.setRewards(rewards);
        campaignDTO.setPromotions(promotions);
        campaignDTO.setContributors(contributors);
        campaignDTO.setNumberOfBackers(contributors.size());
    }

    public List<CampaignDTO> getCampaignByCategory(Long categoryId) {
        List<Campaign> campaigns = this.campaignRepository.findCampaignsByCampaignSubCategoryCampaignCategoryIdAndCampaignStageIn(categoryId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));
        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("There are no campaigns available for this category.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

    public List<CampaignDTO> getCampaignBySubCategory(Long subCategoryId) {
        List<Campaign> campaigns = this.campaignRepository.findCampaignsByCampaignSubCategoryIdAndCampaignStageIn(subCategoryId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));
        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("There are no campaigns available for this sub-category.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

    public List<CampaignDTO> getCampaignsByOwner(String username) {
        List<Campaign> campaigns = this.campaignRepository.findCampaignsByUserUsername(username);
        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("There are no campaigns available for this user.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

    public List<CampaignDTO> searchCampaigns(String searchParam) {
        String[] searchParamArray = searchParam.trim().split("\\W+");
        String searchPattern = String.join("|", searchParamArray);
        List<Campaign> campaigns = this.campaignRepository.searchForCampaigns(searchPattern);
        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("No campaigns found with this search parameter.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

    public List<CampaignDTO> getCampaignsByFundingType(Long fundingTypeId) {
        List<Campaign> campaigns = this.campaignRepository.findCampaignsByFundingTypeIdAndCampaignStage(fundingTypeId, CampaignStage.FUNDING);
        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("There are no campaigns available for this funding type.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

}