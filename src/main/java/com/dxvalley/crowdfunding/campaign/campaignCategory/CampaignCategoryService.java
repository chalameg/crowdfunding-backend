package com.dxvalley.crowdfunding.campaign.campaignCategory;

import com.dxvalley.crowdfunding.campaign.campaignCategory.CampaignCategory;

import java.util.List;

public interface CampaignCategoryService {
    CampaignCategory addCampaignCategory(CampaignCategory campaignCategory);

    CampaignCategory editCampaignCategory(CampaignCategory campaignCategory, Long campaignCategoryId);

    List<CampaignCategory> getCampaignCategories();

    CampaignCategory getCampaignCategoryById(Long campaignCategoryId);

    void deleteCampaignCategory(Long campaignCategoryId);
}
