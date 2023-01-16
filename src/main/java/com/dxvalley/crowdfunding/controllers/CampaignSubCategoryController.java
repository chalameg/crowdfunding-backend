package com.dxvalley.crowdfunding.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dxvalley.crowdfunding.models.CampaignSubCategory;
import com.dxvalley.crowdfunding.services.CampaignSubCategoryService;

@RestController
@RequestMapping("/api/campaignSubCategory")
public class CampaignSubCategoryController {
  private final CampaignSubCategoryService campaignSubCategoryService;

  public CampaignSubCategoryController(CampaignSubCategoryService campaignSubCategoryService) {
    this.campaignSubCategoryService = campaignSubCategoryService;
  }

  @GetMapping("/getSubCategories")
  List<CampaignSubCategory> gCampaignSubcategories() {
    return this.campaignSubCategoryService.getCampaignSubCategories();
  }

  @GetMapping("/{CampaignSubCategoryId}")
  CampaignSubCategory getCampaignSubCategory(@PathVariable Long campaignSubCategoryId) {
    return campaignSubCategoryService.getCampaignSubCategoryById(campaignSubCategoryId);
  }

  @PostMapping("/add")
  CampaignSubCategory addCampaignSubCategory(@RequestBody CampaignSubCategory CampaignSubCategories) {
    return campaignSubCategoryService.addCampaignSubCategory(CampaignSubCategories);
  }

  @PutMapping("/edit/{campaignSubCategoryId}")
  CampaignSubCategory ediCampaignSubCategory(@RequestBody CampaignSubCategory tempSubCampaignCategory,
      @PathVariable Long campaignSubCategoryId) {
    CampaignSubCategory CampaignSubCategory = this.campaignSubCategoryService
        .getCampaignSubCategoryById(campaignSubCategoryId);
    CampaignSubCategory.setName(tempSubCampaignCategory.getName());
    CampaignSubCategory.setDescription(tempSubCampaignCategory.getDescription());
    return campaignSubCategoryService.editCampaignSubCategory(CampaignSubCategory);
  }

  @DeleteMapping("/delete/{campaignSubCategoryId}")
  void deleteCampaignCategory(@PathVariable Long campaignSubCategoryId) {
    campaignSubCategoryService.deleteCampaignSubCategory(campaignSubCategoryId);
  }
}
