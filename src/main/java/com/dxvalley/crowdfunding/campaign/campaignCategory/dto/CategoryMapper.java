package com.dxvalley.crowdfunding.campaign.campaignCategory.dto;

import com.dxvalley.crowdfunding.campaign.campaignCategory.CampaignCategory;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.dto.SubCategoryRes;

import java.util.List;

public class CategoryMapper {
    public static CategoryResponse toCategoryResponse(CampaignCategory campaignCategory, List<SubCategoryRes> subCategories) {
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(campaignCategory.getId());
        categoryResponse.setName(campaignCategory.getName());
        categoryResponse.setDescription(campaignCategory.getDescription());
        categoryResponse.setSubCategories(subCategories);
        return categoryResponse;
    }
}
