package com.dxvalley.crowdfunding.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/campaignCategorys")
public class CampaignCategoryController {
  private final CampaignCategoryService campaignCategoryService;

  public CampaignCategoryController(CampaignCategoryService campaignCategoryService) {
    this.campaignCategoryService = campaignCategoryService;
  }

  @GetMapping
  List<CampaignCategory> getcampaignCategories() {
    return this.campaignCategoryService.getCampaignCategories();
  }

  @GetMapping("/{campaignCategoryId}")
  CampaignCategory getcampaignCategory(@PathVariable Long campaignCategoryId) {
    return campaignCategoryService.getCampaignCategoryById(campaignCategoryId);
  }

  @PostMapping
  CampaignCategory addCategory(@RequestBody CampaignCategory campaignCategories) {
    return campaignCategoryService.addCampaignCategory(campaignCategories);
  }

  @PutMapping("/{campaignCategoryId}")
  CampaignCategory editcampaignCategory(@RequestBody CampaignCategory tempcampaignCategory,
      @PathVariable Long campaignCategoryId) {
    CampaignCategory campaignCategory = this.campaignCategoryService.getCampaignCategoryById(campaignCategoryId);
    campaignCategory.setName(tempcampaignCategory.getName());
    campaignCategory.setDescription(tempcampaignCategory.getDescription());
    return campaignCategoryService.editCampaignCategory(campaignCategory);
  }

  @DeleteMapping("/{campaignCategoryId}")
  ResponseEntity<?> deletecampaignCategory(@PathVariable Long campaignCategoryId) {

    CampaignCategory campaignCategory = this.campaignCategoryService.getCampaignCategoryById(campaignCategoryId);

    if(campaignCategory == null) return new ResponseEntity<String>("Entry does not exist!", HttpStatus.BAD_REQUEST);

    campaignCategoryService.deleteCampaignCategory(campaignCategoryId);

    ApiResponse response = new ApiResponse("success", "CampaignCategory Deleted successfully!");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
