package com.dxvalley.crowdfunding.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.models.CampaignSubCategory;
import com.dxvalley.crowdfunding.repositories.CampaignSubCategoryRepository;
import com.dxvalley.crowdfunding.services.CampaignSubCategoryService;

@Service
public class CampaignSubcategoryServiceImpl implements CampaignSubCategoryService {
    private final CampaignSubCategoryRepository campaignSubCategoryRepository;

    public CampaignSubcategoryServiceImpl(CampaignSubCategoryRepository campaignSubCategoryRepository) {
        this.campaignSubCategoryRepository = campaignSubCategoryRepository;
    }
    @Override
    public CampaignSubCategory addCampaignSubCategory(CampaignSubCategory campaignSubCategory) {
        return this.campaignSubCategoryRepository.save(campaignSubCategory);
    }

    @Override
    public CampaignSubCategory editCampaignSubCategory(CampaignSubCategory campaignSubCategory) {
        return this.campaignSubCategoryRepository.save(campaignSubCategory);
    }

    @Override
    public List<CampaignSubCategory> getCampaignSubCategories() {
        return this.campaignSubCategoryRepository.findAll();
    }

    @Override
    public CampaignSubCategory getCampaignSubCategoryById(Long campaignSubCategoryId) {
        return this.campaignSubCategoryRepository.findCampaignSubCategoryByCampaignSubCategoryId(campaignSubCategoryId);
    }

    @Override
    public void deleteCampaignSubCategory(Long campaignSubCategoryId) {
        campaignSubCategoryRepository.deleteById(campaignSubCategoryId);
    }
    
}
