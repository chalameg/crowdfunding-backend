package com.dxvalley.crowdfunding.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/api/campaignCategories")
public class CampaignCategoryController {
  private final CampaignCategoryService campaignCategoryService;

  @GetMapping
  List<CampaignCategory> getCampaignCategories() {
    return campaignCategoryService.getCampaignCategories();
  }
  @GetMapping("/{campaignCategoryId}")
  ResponseEntity getCampaignCategory(@PathVariable Long campaignCategoryId) {
    return campaignCategoryService.getCampaignCategoryById(campaignCategoryId);
  }

  @PostMapping
  ResponseEntity<?> addCategory(@RequestBody CampaignCategory campaignCategories) {
    return campaignCategoryService.addCampaignCategory(campaignCategories);
  }
  @PutMapping("/{campaignCategoryId}")
  ResponseEntity<?> editCampaignCategory(@RequestBody CampaignCategory tempCampaignCategory,
      @PathVariable Long campaignCategoryId) {
    return  campaignCategoryService.editCampaignCategory(tempCampaignCategory,campaignCategoryId);
  }
  @DeleteMapping("/{campaignCategoryId}")
  ResponseEntity<?> deleteCampaignCategory(@PathVariable Long campaignCategoryId) {
   return campaignCategoryService.deleteCampaignCategory(campaignCategoryId);
  }
}
