package com.dxvalley.crowdfunding.services;

import java.util.List;

import com.dxvalley.crowdfunding.models.CampaignCategory;
import org.springframework.http.ResponseEntity;

public interface CampaignCategoryService {
    ResponseEntity<?> addCampaignCategory (CampaignCategory campaignCategory);
    ResponseEntity<?> editCampaignCategory (CampaignCategory campaignCategory,Long campaignCategoryId);
    List<CampaignCategory> getCampaignCategories ();
    ResponseEntity<?> getCampaignCategoryById(Long campaignCategoryId);
    ResponseEntity<?> deleteCampaignCategory(Long campaignCategoryId);
}
