package com.dxvalley.crowdfunding.controllers;

import com.dxvalley.crowdfunding.dto.ApiResponse;
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

    @GetMapping("/getSubCategories")
    ResponseEntity<?> getCampaignSubcategories() {
        var subCategories = campaignSubCategoryService.getCampaignSubCategories();
        return new ResponseEntity<>(subCategories, HttpStatus.OK);
    }

    @GetMapping("/{CampaignSubCategoryId}")
    ResponseEntity<?> getCampaignSubCategory(@PathVariable Long CampaignSubCategoryId) {
        var subCategory = campaignSubCategoryService.getCampaignSubCategoryById(CampaignSubCategoryId);
        return new ResponseEntity<>(subCategory, HttpStatus.OK);
    }

    @GetMapping("/getByCategoryId/{campaignCategoryId}")
    ResponseEntity<?> getCampaignSubCategoryByCategory(@PathVariable Long campaignCategoryId) {
        var subCategories = campaignSubCategoryService.getCampaignSubCategoryByCategory(campaignCategoryId);
        return new ResponseEntity<>(subCategories, HttpStatus.OK);
    }

    @PostMapping("/add")
    ResponseEntity<?> addCampaignSubCategory(@RequestBody CampaignSubCategory campaignSubCategory) {
        var subCategory = campaignSubCategoryService.addCampaignSubCategory(campaignSubCategory);
        return new ResponseEntity<>(subCategory, HttpStatus.OK);
    }

    @PutMapping("/edit/{campaignSubCategoryId}")
    ResponseEntity<?> editCampaignSubCategory(@RequestBody CampaignSubCategory tempSubCampaignCategory, @PathVariable Long campaignSubCategoryId) {
        var subCategory = campaignSubCategoryService.editCampaignSubCategory(campaignSubCategoryId, tempSubCampaignCategory);
        return new ResponseEntity<>(subCategory, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{campaignSubCategoryId}")
    ResponseEntity<?> deleteCampaignCategory(@PathVariable Long campaignSubCategoryId) {
        campaignSubCategoryService.deleteCampaignSubCategory(campaignSubCategoryId);
        ApiResponse response = new ApiResponse("success", "Campaign subCategory Deleted successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

