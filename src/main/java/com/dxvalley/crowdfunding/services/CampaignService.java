package com.dxvalley.crowdfunding.services;

import java.util.List;

import com.dxvalley.crowdfunding.dto.CampaignAddRequestDto;
import com.dxvalley.crowdfunding.dto.CampaignDTO;
import com.dxvalley.crowdfunding.dto.CampaignLikeDTO;
import com.dxvalley.crowdfunding.models.Campaign;


public interface CampaignService {
    Campaign addCampaign(CampaignAddRequestDto campaignAddRequestDto);

    String likeCampaign(CampaignLikeDTO campaignLikeDTO);

    Campaign editCampaign(Campaign campaign);

    List<CampaignDTO> getCampaigns();

    List<CampaignDTO> getEnabledCampaigns();

    Campaign getCampaignById(Long campaignId);

    List<CampaignDTO> getCampaignByCategory(Long categoryId);

    List<CampaignDTO> getCampaignBySubCategory(Long subCategoryId);

    void deleteCampaign(Long campaignId);

    List<CampaignDTO> getCampaignsByOwner(String owner);

    Campaign enableCampaign(Long campaignId);

    List<CampaignDTO> searchCampaigns(String searchParam);

    List<CampaignDTO> getCampaignsByStage(String campaignStage);

    List<CampaignDTO> getCampaignsByFundingType(Long fundingTypeId);
}
