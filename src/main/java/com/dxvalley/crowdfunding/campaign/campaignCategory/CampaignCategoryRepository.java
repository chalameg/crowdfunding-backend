package com.dxvalley.crowdfunding.campaign.campaignCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Short> {
    CampaignCategory findByName(String name);
}





