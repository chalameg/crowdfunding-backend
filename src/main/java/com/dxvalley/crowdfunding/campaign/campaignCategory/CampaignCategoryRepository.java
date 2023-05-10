package com.dxvalley.crowdfunding.campaign.campaignCategory;

import com.dxvalley.crowdfunding.campaign.campaignCategory.CampaignCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Long> {
    Optional<CampaignCategory> findCampaignCategoryByCampaignCategoryId(Long campaignCategoryId);
    CampaignCategory findByName(String name);
}





