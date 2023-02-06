package com.dxvalley.crowdfunding.services;

import java.util.List;

import com.dxvalley.crowdfunding.dto.CampaignAddRequestDto;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.Campaign;


public interface CampaignService {
    Campaign addCampaign (CampaignAddRequestDto campaignAddRequestDto);
    Campaign editCampaign (Campaign campaign);
    List<Campaign> getCampaigns ();
    List<Campaign> getEnabledCampaigns ();
    Campaign getCampaignById(Long campaignId) throws ResourceNotFoundException;
    List<Campaign> getCampaignByCategory(Long categoryId);
    List<Campaign> getCampaignBySubCategory(Long subCategoryId);
    String deleteCampaign( Long campaignId);
    List<Campaign> getCampaignsByOwner(String owner);
    Campaign enableCampaign(Long campaignId);
}
