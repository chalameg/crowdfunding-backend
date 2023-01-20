package com.dxvalley.crowdfunding.controllers;

import java.util.List;

import com.dxvalley.crowdfunding.services.CampaignCategoryService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/api/campaignSubCategory")
public class CampaignSubCategoryController {
  private final CampaignSubCategoryService campaignSubCategoryService;
  private final CampaignCategoryService campaignCategoryService;

  @GetMapping("/getSubCategories")
  List<CampaignSubCategory> getCampaignSubcategories() {
    return this.campaignSubCategoryService.getCampaignSubCategories();
  }

  @GetMapping("/{CampaignSubCategoryId}")
  ResponseEntity<?> getCampaignSubCategory(@PathVariable Long CampaignSubCategoryId) {
    var result =  campaignSubCategoryService.getCampaignSubCategoryById(CampaignSubCategoryId);

    if (result == null){
      return new ResponseEntity<>("There is no subcategory with this ID.", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("/add")
  ResponseEntity<?> addCampaignSubCategory(@RequestBody CampaignSubCategory tempCampaignSubCategories) {
    //TODO: check existence of category before adding subcategory
    var campaignSubCategories =  campaignSubCategoryService.addCampaignSubCategory(tempCampaignSubCategories);
      return new ResponseEntity<>(campaignSubCategories, HttpStatus.OK);
  }

  @PutMapping("/edit/{campaignSubCategoryId}")
  ResponseEntity<?> editCampaignSubCategory(@RequestBody CampaignSubCategory tempSubCampaignCategory,
      @PathVariable Long campaignSubCategoryId) {
    CampaignSubCategory campaignSubCategory = this.campaignSubCategoryService
        .getCampaignSubCategoryById(campaignSubCategoryId);

    if (campaignSubCategory == null){
      return new ResponseEntity<>("There is no subcategory with this ID.", HttpStatus.BAD_REQUEST);
    }

    campaignSubCategory.setName(
            tempSubCampaignCategory.getName() != null && tempSubCampaignCategory.getName().length() > 0?
                    tempSubCampaignCategory.getName() :
                    campaignSubCategory.getName());

    campaignSubCategory.setDescription(
            tempSubCampaignCategory.getDescription() != null && tempSubCampaignCategory.getDescription().length() > 0 ?
                    tempSubCampaignCategory.getDescription() :
                    campaignSubCategory.getDescription());
    //TODO: check existence of category before updating subcategory
    campaignSubCategory.setCategory(
            tempSubCampaignCategory.getCategory() != null ?
                    tempSubCampaignCategory.getCategory() :
                    campaignSubCategory.getCategory());

    var result =  campaignSubCategoryService.editCampaignSubCategory(campaignSubCategory);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
  @DeleteMapping("/delete/{campaignSubCategoryId}")
  ResponseEntity<?> deleteCampaignCategory(@PathVariable Long campaignSubCategoryId) {
    CampaignSubCategory campaignSubCategory = this.campaignSubCategoryService
            .getCampaignSubCategoryById(campaignSubCategoryId);

    if (campaignSubCategory == null){
      return new ResponseEntity<>("There is no subcategory with this ID.", HttpStatus.BAD_REQUEST);
    }
    campaignSubCategoryService.deleteCampaignSubCategory(campaignSubCategoryId);
    return new ResponseEntity<>("Deleted Successfully!", HttpStatus.OK);

  }
}
