package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.CampaignCategory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional(readOnly = true)
public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Long> {
    CampaignCategory findCampaignCategoryByCampaignCategoryId(Long campaignCategoryId);
    CampaignCategory findByName(String name);
}





