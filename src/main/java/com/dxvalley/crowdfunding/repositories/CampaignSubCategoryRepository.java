package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.CampaignSubCategory;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CampaignSubCategoryRepository extends JpaRepository<CampaignSubCategory,Long> {
    CampaignSubCategory findCampaignSubCategoryByCampaignSubCategoryId(Long campaignSubCategoryId);
    @Query("SELECT new CampaignSubCategory(csc.campaignSubCategoryId, csc.name, csc.description)" +
            " from CampaignSubCategory as csc where" +
            " csc.campaignCategory.campaignCategoryId = :campaignCategoryId")
    List<CampaignSubCategory> findByCampaignCategoryId(Long campaignCategoryId);
}


