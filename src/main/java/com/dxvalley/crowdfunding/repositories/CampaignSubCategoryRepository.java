package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dxvalley.crowdfunding.models.CampaignSubCategory;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface CampaignSubCategoryRepository extends JpaRepository<CampaignSubCategory,Long> {
    Optional<CampaignSubCategory> findCampaignSubCategoryByCampaignSubCategoryId(Long campaignSubCategoryId);
    CampaignSubCategory findByName(String name);
    @Query("SELECT new CampaignSubCategory(csc.campaignSubCategoryId, csc.name, csc.description)" +
            " from CampaignSubCategory as csc where" +
            " csc.campaignCategory.campaignCategoryId = :campaignCategoryId")
    Optional<List<CampaignSubCategory>> findCampaignSubCategoryByCampaignCategoryId(Long campaignCategoryId);
}


