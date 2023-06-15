package com.dxvalley.crowdfunding.campaign.campaignCategory;

import com.dxvalley.crowdfunding.campaign.campaignCategory.dto.CategoryReq;
import com.dxvalley.crowdfunding.campaign.campaignCategory.dto.CategoryResponse;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campaignCategories")
public class CampaignCategoryController {
    private final CampaignCategoryService campaignCategoryService;

    @GetMapping
    ResponseEntity<List<CategoryResponse>> getCampaignCategories() {
        return ResponseEntity.ok(campaignCategoryService.getCampaignCategories());
    }

    @GetMapping("/{campaignCategoryId}")
    ResponseEntity<CategoryResponse> getCampaignCategory(@PathVariable Short campaignCategoryId) {
        CategoryResponse category = campaignCategoryService.getCampaignCategoryById(campaignCategoryId);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    ResponseEntity<CampaignCategory> addCategory(@RequestBody @Valid CategoryReq categoryReq) {
        CampaignCategory category = campaignCategoryService.addCampaignCategory(categoryReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PutMapping("/{campaignCategoryId}")
    ResponseEntity<CampaignCategory> editCampaignCategory(@RequestBody CampaignCategory tempCampaignCategory, @PathVariable Short campaignCategoryId) {
        CampaignCategory category = campaignCategoryService.editCampaignCategory(tempCampaignCategory, campaignCategoryId);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{campaignCategoryId}")
    ResponseEntity<ApiResponse> deleteCampaignCategory(@PathVariable Short campaignCategoryId) {
        return campaignCategoryService.deleteCampaignCategory(campaignCategoryId);
    }
}
