package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.CampaignSubCategory;


public interface CampaignSubCategoryRepository extends JpaRepository<CampaignSubCategory,Long> {
    CampaignSubCategory findCampaignSubCategoryByCampaignSubCategoryId(Long campaignSubCategoryId);
}
