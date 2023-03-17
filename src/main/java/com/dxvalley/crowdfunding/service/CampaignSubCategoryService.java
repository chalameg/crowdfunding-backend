package com.dxvalley.crowdfunding.service;

import com.dxvalley.crowdfunding.model.CampaignSubCategory;

import java.util.List;

public interface CampaignSubCategoryService {
    CampaignSubCategory addCampaignSubCategory(CampaignSubCategory campaignSubCategory);

    CampaignSubCategory editCampaignSubCategory(Long campaignSubCategoryId, CampaignSubCategory campaignSubCategory);

    List<CampaignSubCategory> getCampaignSubCategories();

    CampaignSubCategory getCampaignSubCategoryById(Long campaignSubCategoryId);

    void deleteCampaignSubCategory(Long campaignSubCategoryId);

    List<CampaignSubCategory> getCampaignSubCategoryByCategory(Long campaignCategoryId);
}
