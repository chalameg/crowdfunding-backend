package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;

import java.util.List;

public interface CampaignRetrievalService {
    CampaignDTO getCampaignById(Long campaignId);

    List<CampaignDTO> getCampaignByCategory(Long categoryId);

    List<CampaignDTO> getCampaignBySubCategory(Long subCategoryId);

    List<CampaignDTO> getCampaignsByOwner(String owner);

    List<CampaignDTO> getCampaignsByStage(String campaignStage);

    List<CampaignDTO> getCampaignsByFundingType(Long fundingTypeId);

    List<CampaignDTO> searchCampaigns(String searchParam);

    List<CampaignDTO> getCampaigns();

}