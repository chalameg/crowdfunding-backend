package com.dxvalley.crowdfunding.services;

import java.util.List;

import com.dxvalley.crowdfunding.models.CampaignCategory;

public interface CampaignCategoryService {
    CampaignCategory addCampaignCategory (CampaignCategory campaignCategory);
    CampaignCategory editCampaignCategory (CampaignCategory campaignCategory,Long campaignCategoryId);
    List<CampaignCategory> getCampaignCategories ();
    CampaignCategory getCampaignCategoryById(Long campaignCategoryId);
    String deleteCampaignCategory(Long campaignCategoryId);
}
