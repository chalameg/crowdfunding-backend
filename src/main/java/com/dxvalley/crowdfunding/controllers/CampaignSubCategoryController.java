package com.dxvalley.crowdfunding.controllers;

import com.dxvalley.crowdfunding.repositories.CampaignSubCategoryRepository;
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

import com.dxvalley.crowdfunding.models.CampaignSubCategory;
import com.dxvalley.crowdfunding.services.CampaignSubCategoryService;

@RestController
@RequestMapping("/api/campaignSubCategory")
public class CampaignSubCategoryController {
  @Autowired
  private CampaignSubCategoryService campaignSubCategoryService;
  @Autowired
  private CampaignSubCategoryRepository campaignSubCategoryRepository;

  @GetMapping("/getSubCategories")
  ResponseEntity<?> getCampaignSubcategories() {
    return new ResponseEntity<>(
            campaignSubCategoryService.getCampaignSubCategories(),
            HttpStatus.OK);
  }

  @GetMapping("/{CampaignSubCategoryId}")
  ResponseEntity<?> getCampaignSubCategory(@PathVariable Long CampaignSubCategoryId) {
    return new ResponseEntity<>(
            campaignSubCategoryService.getCampaignSubCategoryById(CampaignSubCategoryId),
            HttpStatus.OK);
  }

  @GetMapping("/getByCategoryId/{campaignCategoryId}")
  ResponseEntity<?> getCampaignSubCategoryByCategory(@PathVariable Long campaignCategoryId) {
    return new ResponseEntity<>(
            campaignSubCategoryService.getCampaignSubCategoryByCategory(campaignCategoryId),
            HttpStatus.OK);
  }

  @PostMapping("/add")
  ResponseEntity<?> addCampaignSubCategory(@RequestBody CampaignSubCategory campaignSubCategory) {
      return new ResponseEntity<>(
              campaignSubCategoryService.addCampaignSubCategory(campaignSubCategory),
              HttpStatus.OK);
  }

  @PutMapping("/edit/{campaignSubCategoryId}")
  ResponseEntity<?> editCampaignSubCategory(
          @RequestBody CampaignSubCategory tempSubCampaignCategory,
          @PathVariable Long campaignSubCategoryId) {
    return new ResponseEntity<>(
            campaignSubCategoryService.editCampaignSubCategory(campaignSubCategoryId,tempSubCampaignCategory),
            HttpStatus.OK);
  }
  @DeleteMapping("/delete/{campaignSubCategoryId}")
  ResponseEntity<?> deleteCampaignCategory(@PathVariable Long campaignSubCategoryId) {
    return new ResponseEntity<>(
            campaignSubCategoryService.deleteCampaignSubCategory(campaignSubCategoryId),
            HttpStatus.OK);
  }
  }

