package com.dxvalley.crowdfunding.campaign.campaignSubCategory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CampaignSubCategoryRepository extends JpaRepository<CampaignSubCategory, Short> {
    CampaignSubCategory findByName(String name);

    List<CampaignSubCategory> findByCampaignCategoryId(Short campaignCategoryId);

}


