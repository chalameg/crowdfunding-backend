package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.CampaignCategory;


public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Long> {
    CampaignCategory findCampaignCategoryByCampaignCategoryId(Long campaignCategoryId);
}
