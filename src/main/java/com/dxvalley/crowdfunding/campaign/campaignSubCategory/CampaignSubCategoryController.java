package com.dxvalley.crowdfunding.campaign.campaignSubCategory;

import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ApiResponse.success( "Campaign subCategory Deleted successfully!");
    }
}

