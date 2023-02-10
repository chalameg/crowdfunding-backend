package com.dxvalley.crowdfunding.services.impl;

import java.util.List;

import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.repositories.CampaignSubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dxvalley.crowdfunding.services.CampaignCategoryService;
import com.dxvalley.crowdfunding.models.CampaignCategory;
import com.dxvalley.crowdfunding.repositories.CampaignCategoryRepository;

@Service
public class CampaignCategoryServiceIml implements CampaignCategoryService {
    @Autowired
    private  CampaignCategoryRepository campaignCategoryRepository;
    @Autowired
    private CampaignSubCategoryRepository campaignSubCategoryRepository;

    @Override
    public CampaignCategory addCampaignCategory(CampaignCategory tempCampaignCategory) {
        var campaignCategory = campaignCategoryRepository.findByName(tempCampaignCategory.getName());

        return campaignCategoryRepository.save(tempCampaignCategory);

    }

    @Override
    public CampaignCategory editCampaignCategory(CampaignCategory tempCampaignCategory, Long campaignCategoryId) {
        CampaignCategory campaignCategory = campaignCategoryRepository
                .findCampaignCategoryByCampaignCategoryId(campaignCategoryId).orElseThrow(
                        () -> new ResourceNotFoundException("There is no campaign Category with this ID.")
                );

        campaignCategory.setName(
                tempCampaignCategory.getName() != null ?
                        tempCampaignCategory.getName() :
                        campaignCategory.getName());

        campaignCategory.setDescription(
                tempCampaignCategory.getDescription() != null ?
                        tempCampaignCategory.getDescription() :
                        campaignCategory.getDescription());

        return  campaignCategoryRepository.save(campaignCategory);
    }

    @Override
    public List<CampaignCategory> getCampaignCategories() {
        var campaignCategories = campaignCategoryRepository.findAll();
        if(campaignCategories.size() == 0){
            throw new ResourceNotFoundException("Currently, There is no Campaign Category.");
        }
        for (CampaignCategory campaignCategory : campaignCategories) {
            var subCategory = campaignSubCategoryRepository.
                    findCampaignSubCategoryByCampaignCategoryId(campaignCategory.getCampaignCategoryId()).get();
            campaignCategory.setCampaignSubCategories(subCategory);
        }
        return campaignCategories;
    }

    @Override
    public CampaignCategory getCampaignCategoryById(Long campaignCategoryId) {
        CampaignCategory campaignCategory = campaignCategoryRepository
                .findCampaignCategoryByCampaignCategoryId(campaignCategoryId).orElseThrow(
                        () -> new ResourceNotFoundException("There is no campaign Category with this ID.")
                );

        var subCategory =  campaignSubCategoryRepository.findCampaignSubCategoryByCampaignCategoryId(campaignCategoryId).get();
        campaignCategory.setCampaignSubCategories(subCategory);
      return campaignCategory;
    }

    @Override
    public String deleteCampaignCategory(Long campaignCategoryId) {
        CampaignCategory campaignCategory = campaignCategoryRepository
                .findCampaignCategoryByCampaignCategoryId(campaignCategoryId).orElseThrow(
                        () -> new ResourceNotFoundException("There is no campaign Category with this ID.")
                );
        campaignCategoryRepository.delete(campaignCategory);
        return "CampaignCategory Deleted successfully!";
    }
}
