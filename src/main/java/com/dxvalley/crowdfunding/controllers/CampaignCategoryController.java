package com.dxvalley.crowdfunding.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/campaignCategories")
public class CampaignCategoryController {
  @Autowired
  private CampaignCategoryService campaignCategoryService;

  @GetMapping
  ResponseEntity<?> getCampaignCategories() {
    return new ResponseEntity<>(
            campaignCategoryService.getCampaignCategories(),
            HttpStatus.OK);
  }

  @GetMapping("/{campaignCategoryId}")
  ResponseEntity<?> getCampaignCategory(@PathVariable Long campaignCategoryId) {
    return new ResponseEntity<>(
            campaignCategoryService.getCampaignCategoryById(campaignCategoryId),
            HttpStatus.OK);
  }

  @PostMapping
  ResponseEntity<?> addCategory(@RequestBody CampaignCategory campaignCategories) {
    return new ResponseEntity<>(
            campaignCategoryService.addCampaignCategory(campaignCategories),
            HttpStatus.OK);
  }

  @PutMapping("/{campaignCategoryId}")
  ResponseEntity<?> editCampaignCategory(
          @RequestBody CampaignCategory tempCampaignCategory,
          @PathVariable Long campaignCategoryId) {
    return  new ResponseEntity<>(
            campaignCategoryService.editCampaignCategory(tempCampaignCategory,
                    campaignCategoryId), HttpStatus.OK);
  }

  @DeleteMapping("/{campaignCategoryId}")
  ResponseEntity<?> deleteCampaignCategory(@PathVariable Long campaignCategoryId) {
   return new ResponseEntity<>(
           campaignCategoryService.deleteCampaignCategory(campaignCategoryId),
           HttpStatus.OK);
  }
}
