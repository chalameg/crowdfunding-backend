package com.dxvalley.crowdfunding.campaign.campaignSubCategory;

import com.dxvalley.crowdfunding.campaign.campaignSubCategory.dto.SubCategoryReq;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.dto.SubCategoryRes;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaignSubCategory")
@RequiredArgsConstructor
public class CampaignSubCategoryController {

    private final CampaignSubCategoryService campaignSubCategoryService;

    @GetMapping({"/getSubCategories"})
    ResponseEntity<List<SubCategoryRes>> getCampaignSubcategories() {
        List<SubCategoryRes> subCategories = this.campaignSubCategoryService.getCampaignSubCategories();
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping({"/{CampaignSubCategoryId}"})
    ResponseEntity<CampaignSubCategory> getCampaignSubCategory(@PathVariable Short CampaignSubCategoryId) {
        CampaignSubCategory subCategory = this.campaignSubCategoryService.getCampaignSubCategoryById(CampaignSubCategoryId);
        return ResponseEntity.ok(subCategory);
    }

    @GetMapping({"/getByCategoryId/{campaignCategoryId}"})
    ResponseEntity<List<SubCategoryRes>> getCampaignSubCategoryByCategory(@PathVariable Short campaignCategoryId) {
        List<SubCategoryRes> subCategories = this.campaignSubCategoryService.getCampaignSubCategoryByCategory(campaignCategoryId);
        return ResponseEntity.ok(subCategories);
    }

    @PostMapping({"/add"})
    ResponseEntity<CampaignSubCategory> addCampaignSubCategory(@RequestBody @Valid SubCategoryReq subCategoryReq) {
        CampaignSubCategory subCategory = this.campaignSubCategoryService.addCampaignSubCategory(subCategoryReq);
        return ResponseEntity.ok(subCategory);
    }

    @PutMapping({"/edit/{campaignSubCategoryId}"})
    ResponseEntity<CampaignSubCategory> editCampaignSubCategory(@RequestBody CampaignSubCategory tempSubCampaignCategory, @PathVariable Short campaignSubCategoryId) {
        CampaignSubCategory subCategory = this.campaignSubCategoryService.editCampaignSubCategory(campaignSubCategoryId, tempSubCampaignCategory);
        return ResponseEntity.ok(subCategory);
    }

    @DeleteMapping({"/delete/{campaignSubCategoryId}"})
    ResponseEntity<ApiResponse> deleteCampaignCategory(@PathVariable Short campaignSubCategoryId) {
        return this.campaignSubCategoryService.deleteCampaignSubCategory(campaignSubCategoryId);
    }
}
