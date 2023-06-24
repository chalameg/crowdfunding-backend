package com.dxvalley.crowdfunding.campaign.campaignCategory;

import com.dxvalley.crowdfunding.campaign.campaignCategory.dto.CategoryReq;
import com.dxvalley.crowdfunding.campaign.campaignCategory.dto.CategoryResponse;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CampaignCategoryService {
    CampaignCategory addCampaignCategory(CategoryReq categoryReq);

    CampaignCategory editCampaignCategory(CampaignCategory campaignCategory, Short campaignCategoryId);

    List<CategoryResponse> getCampaignCategories();

    CategoryResponse getCampaignCategoryById(Short campaignCategoryId);

    ResponseEntity<ApiResponse> deleteCampaignCategory(Short campaignCategoryId);
}