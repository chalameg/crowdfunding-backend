package com.dxvalley.crowdfunding.campaign.campaignSubCategory;

import com.dxvalley.crowdfunding.campaign.campaignSubCategory.dto.SubCategoryReq;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.dto.SubCategoryRes;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CampaignSubCategoryService {
    CampaignSubCategory addCampaignSubCategory(SubCategoryReq subCategoryReq);

    CampaignSubCategory editCampaignSubCategory(Short campaignSubCategoryId, CampaignSubCategory campaignSubCategory);

    List<SubCategoryRes> getCampaignSubCategories();

    CampaignSubCategory getCampaignSubCategoryById(Short campaignSubCategoryId);

    List<SubCategoryRes> getCampaignSubCategoryByCategory(Short campaignCategoryId);

    ResponseEntity<ApiResponse> deleteCampaignSubCategory(Short campaignSubCategoryId);
}
