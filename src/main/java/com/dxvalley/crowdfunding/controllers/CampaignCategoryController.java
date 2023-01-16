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

import com.dxvalley.crowdfunding.models.CampaignCategory;
import com.dxvalley.crowdfunding.services.CampaignCategoryService;

@RestController
@RequestMapping("/api/campaignCategory")
public class CampaignCategoryController {
  private final CampaignCategoryService campaignCategoryService;

  public CampaignCategoryController(CampaignCategoryService campaignCategoryService) {
    this.campaignCategoryService = campaignCategoryService;
  }

  @GetMapping("/getCategories")
  List<CampaignCategory> getcampaignCategories() {
    return this.campaignCategoryService.getCampaignCategories();
  }

  @GetMapping("/{campaignCategoryId}")
  CampaignCategory getcampaignCategory(@PathVariable Long campaignCategoryId) {
    return campaignCategoryService.getCampaignCategoryById(campaignCategoryId);
  }

  @PostMapping("/add")
  CampaignCategory addCategory(@RequestBody CampaignCategory campaignCategories) {
    return campaignCategoryService.addCampaignCategory(campaignCategories);
  }

  @PutMapping("/edit/{campaignCategoryId}")
  CampaignCategory editcampaignCategory(@RequestBody CampaignCategory tempcampaignCategory,
      @PathVariable Long campaignCategoryId) {
    CampaignCategory campaignCategory = this.campaignCategoryService.getCampaignCategoryById(campaignCategoryId);
    campaignCategory.setName(tempcampaignCategory.getName());
    campaignCategory.setDescription(tempcampaignCategory.getDescription());
    return campaignCategoryService.editCampaignCategory(campaignCategory);
  }

  @DeleteMapping("/delete/{campaignCategoryId}")
  void deletecampaignCategory(@PathVariable Long campaignCategoryId) {
    campaignCategoryService.deleteCampaignCategory(campaignCategoryId);
  }
}
