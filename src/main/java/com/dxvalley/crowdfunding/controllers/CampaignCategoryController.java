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

import com.dxvalley.crowdfunding.models.CampaignCategory;
import com.dxvalley.crowdfunding.services.CampaignCategoryService;

@RestController
@RequestMapping("/api/campaignCategories")
public class CampaignCategoryController {
    @Autowired
    private CampaignCategoryService campaignCategoryService;

    @GetMapping
    ResponseEntity<?> getCampaignCategories() {
        var categories = campaignCategoryService.getCampaignCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{campaignCategoryId}")
    ResponseEntity<?> getCampaignCategory(@PathVariable Long campaignCategoryId) {
        var category = campaignCategoryService.getCampaignCategoryById(campaignCategoryId);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping
    ResponseEntity<?> addCategory(@RequestBody CampaignCategory campaignCategories) {
        var category = campaignCategoryService.addCampaignCategory(campaignCategories);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PutMapping("/{campaignCategoryId}")
    ResponseEntity<?> editCampaignCategory(@RequestBody CampaignCategory tempCampaignCategory, @PathVariable Long campaignCategoryId) {
        var category = campaignCategoryService.editCampaignCategory(tempCampaignCategory, campaignCategoryId);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @DeleteMapping("/{campaignCategoryId}")
    ResponseEntity<?> deleteCampaignCategory(@PathVariable Long campaignCategoryId) {
        campaignCategoryService.deleteCampaignCategory(campaignCategoryId);
        ApiResponse response = new ApiResponse("success", "CampaignCategory Deleted successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
