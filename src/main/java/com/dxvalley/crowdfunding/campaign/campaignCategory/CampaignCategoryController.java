package com.dxvalley.crowdfunding.campaign.campaignCategory;

import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ApiResponse.success("CampaignCategory Deleted successfully!");
    }
}
