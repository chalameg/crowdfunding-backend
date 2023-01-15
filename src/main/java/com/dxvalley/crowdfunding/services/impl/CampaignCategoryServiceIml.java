package com.dxvalley.crowdfunding.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.services.CampaignCategoryService;
import com.dxvalley.crowdfunding.models.CampaignCategory;
import com.dxvalley.crowdfunding.repositories.CampaignCategoryRepository;

@Service
public class CampaignCategoryServiceIml implements CampaignCategoryService {

    private final CampaignCategoryRepository CampaignCategoryRepository;

    public CampaignCategoryServiceIml(CampaignCategoryRepository campaignCategoryRepository) {
        this.CampaignCategoryRepository = campaignCategoryRepository;
    }

    @Override
    public CampaignCategory addCampaignCategory(CampaignCategory campaignCategory) {
        return this.CampaignCategoryRepository.save(campaignCategory);
    }

    @Override
    public CampaignCategory editCampaignCategory(CampaignCategory campaignCategory) {
        return this.CampaignCategoryRepository.save(campaignCategory);
    }

    @Override
    public List<CampaignCategory> getCampaignCategories() {
        return this.CampaignCategoryRepository.findAll();
    }

    @Override
    public CampaignCategory getCampaignCategoryById(Long campaignCategoryId) {
        return this.CampaignCategoryRepository.findCampaignCategoryByCampaignCategoryId(campaignCategoryId);
    }

    @Override
    public void deleteCampaignCategory(Long campaignCategoryId) {
        CampaignCategoryRepository.deleteById(campaignCategoryId);

    }

}
