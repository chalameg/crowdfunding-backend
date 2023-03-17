package com.dxvalley.crowdfunding.service.impl;

import com.dxvalley.crowdfunding.exception.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.model.CampaignSubCategory;
import com.dxvalley.crowdfunding.repository.CampaignSubCategoryRepository;
import com.dxvalley.crowdfunding.service.CampaignCategoryService;
import com.dxvalley.crowdfunding.service.CampaignSubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignSubCategoryServiceImpl implements CampaignSubCategoryService {
    @Autowired
    private CampaignSubCategoryRepository campaignSubCategoryRepository;
    @Autowired
    private CampaignCategoryService campaignCategoryService;

    @Override
    public CampaignSubCategory addCampaignSubCategory(CampaignSubCategory campaignSubCategories) {
        var subCategory = campaignSubCategoryRepository.findByName(campaignSubCategories.getName());
        if (subCategory != null) {
            throw new ResourceAlreadyExistsException("There is already a sub-category with this name!");
        }
        campaignCategoryService.getCampaignCategoryById(campaignSubCategories.getCampaignCategory().getCampaignCategoryId()); // To check existence of category for subcategory
        return this.campaignSubCategoryRepository.save(campaignSubCategories);
    }

    @Override
    public CampaignSubCategory editCampaignSubCategory(Long campaignSubCategoryId, CampaignSubCategory tempcampaignSubCategory) {

        var campaignSubCategory =
                campaignSubCategoryRepository.findById(campaignSubCategoryId).orElseThrow(
                        () -> new ResourceNotFoundException("There is no campaign subcategory with this ID."
                        ));

        var campaignCategory = campaignCategoryService.getCampaignCategoryById(
                tempcampaignSubCategory.getCampaignCategory().getCampaignCategoryId());

        campaignSubCategory.setName(
                tempcampaignSubCategory.getName() != null && tempcampaignSubCategory.getName().length() > 0 ?
                        tempcampaignSubCategory.getName() :
                        campaignSubCategory.getName());

        campaignSubCategory.setDescription(
                tempcampaignSubCategory.getDescription() != null && tempcampaignSubCategory.getDescription().length() > 0 ?
                        tempcampaignSubCategory.getDescription() :
                        campaignSubCategory.getDescription());

        campaignSubCategory.setCampaignCategory(campaignCategory);
        return campaignSubCategoryRepository.save(campaignSubCategory);
    }

    @Override
    public List<CampaignSubCategory> getCampaignSubCategories() {
        var campaignSubCategories = campaignSubCategoryRepository.findAll();
        if (campaignSubCategories.size() == 0) {
            throw new ResourceNotFoundException("Currently, There is no Campaign SubCategory.");
        }
        return campaignSubCategories;
    }

    @Override
    public CampaignSubCategory getCampaignSubCategoryById(Long campaignSubCategoryId) {
        return campaignSubCategoryRepository
                .findCampaignSubCategoryByCampaignSubCategoryId(campaignSubCategoryId).orElseThrow(
                        () -> new ResourceNotFoundException("There is no campaign subcategory with this ID.")
                );
    }

    @Override
    public List<CampaignSubCategory> getCampaignSubCategoryByCategory(Long campaignCategoryId) {
        var campaignSubCategories = campaignSubCategoryRepository.
                findCampaignSubCategoryByCampaignCategoryId(campaignCategoryId).orElseThrow(
                        () -> new ResourceNotFoundException("There is no campaign subcategory with this ID.")
                );

        return campaignSubCategories;
    }

    @Override
    public void deleteCampaignSubCategory(Long campaignSubCategoryId) {
        var campaignSubCategory = campaignSubCategoryRepository.findById(campaignSubCategoryId).orElseThrow(
                () -> new ResourceNotFoundException("There is no campaign subcategory with this ID.")
        );
        campaignSubCategoryRepository.delete(campaignSubCategory);
    }
}
