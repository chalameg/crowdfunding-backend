package com.dxvalley.crowdfunding.services;

import java.util.List;

import com.dxvalley.crowdfunding.models.CampaignSubCategory;

public interface CampaignSubCategoryService {
    CampaignSubCategory addCampaignSubCategory (CampaignSubCategory campaignSubCategory);
    CampaignSubCategory editCampaignSubCategory (CampaignSubCategory campaignSubCategory);
    List<CampaignSubCategory> getCampaignSubCategories ();
    CampaignSubCategory getCampaignSubCategoryById(Long campaignSubCategoryId);
    void deleteCampaignSubCategory( Long campaignSubCategoryId);
}
