package com.dxvalley.crowdfunding.services.impl;

import java.util.List;


import com.dxvalley.crowdfunding.repositories.CampaignSubCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.services.CampaignCategoryService;
import com.dxvalley.crowdfunding.models.CampaignCategory;
import com.dxvalley.crowdfunding.repositories.CampaignCategoryRepository;

@Service
@AllArgsConstructor
public class CampaignCategoryServiceIml implements CampaignCategoryService {
    private final CampaignCategoryRepository campaignCategoryRepository;
    private final CampaignSubCategoryRepository campaignSubCategoryRepository;
    @Override
    public ResponseEntity<?> addCampaignCategory(CampaignCategory tempCampaignCategory) {
        var campaignCategory = campaignCategoryRepository.findByName(tempCampaignCategory.getName());
        if(campaignCategory != null){
            return new ResponseEntity<>(
                    tempCampaignCategory.getName() + " Category already exist!",
                    HttpStatus.BAD_REQUEST);
        }
        var result = campaignCategoryRepository.save(tempCampaignCategory);
        return new ResponseEntity<>(
                result,
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> editCampaignCategory(CampaignCategory tempCampaignCategory, Long campaignCategoryId) {
        CampaignCategory campaignCategory = campaignCategoryRepository.findCampaignCategoryByCampaignCategoryId(campaignCategoryId);
        if(campaignCategory == null){
            return new ResponseEntity<>(
                    "Category does not exist with this ID!",
                    HttpStatus.BAD_REQUEST);

        }
        campaignCategory.setName(
                tempCampaignCategory.getName() != null ?
                        tempCampaignCategory.getName() :
                        campaignCategory.getName());

        campaignCategory.setDescription(
                tempCampaignCategory.getDescription() != null ?
                        tempCampaignCategory.getDescription() :
                        campaignCategory.getDescription());

        var category =  campaignCategoryRepository.save(campaignCategory);
        return new ResponseEntity<>(
                category,
                HttpStatus.OK);
    }

    @Override
    public List<CampaignCategory> getCampaignCategories() {
        return this.campaignCategoryRepository.findAll();
    }

    @Override
    public ResponseEntity<?> getCampaignCategoryById(Long campaignCategoryId) {
        CampaignCategory campaignCategory = campaignCategoryRepository.findCampaignCategoryByCampaignCategoryId(campaignCategoryId);
        if(campaignCategory == null){
            return new ResponseEntity<>(
                    "Category does not exist with this ID!",
                    HttpStatus.BAD_REQUEST);

        }
        var subCategory = campaignSubCategoryRepository.findByCampaignCategoryId(campaignCategoryId);
        campaignCategory.setCampaignSubCategories(subCategory);
        return new ResponseEntity<>(
                campaignCategory,
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteCampaignCategory(Long campaignCategoryId) {
        CampaignCategory campaignCategory = campaignCategoryRepository.findCampaignCategoryByCampaignCategoryId(campaignCategoryId);
        if(campaignCategory == null){
            return new ResponseEntity<>(
                    "Category does not exist with this ID!",
                    HttpStatus.BAD_REQUEST);

        }
        campaignCategoryRepository.deleteById(campaignCategoryId);
        return new ResponseEntity<>(
                "CampaignCategory Deleted successfully!",
                HttpStatus.OK);
    }
}
