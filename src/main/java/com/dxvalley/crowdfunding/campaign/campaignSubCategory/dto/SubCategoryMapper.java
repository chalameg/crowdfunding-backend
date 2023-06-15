package com.dxvalley.crowdfunding.campaign.campaignSubCategory.dto;

import com.dxvalley.crowdfunding.campaign.campaignSubCategory.CampaignSubCategory;

public class SubCategoryMapper {
    public static SubCategoryRes toSubCategoryRes(CampaignSubCategory subCategory) {
        SubCategoryRes categoryResponse = new SubCategoryRes();
        categoryResponse.setId(subCategory.getId());
        categoryResponse.setName(subCategory.getName());
        categoryResponse.setDescription(subCategory.getDescription());

        return categoryResponse;
    }
}
