package com.dxvalley.crowdfunding.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, Long>{
    Campaign findCampaignByCampaignId(Long campaignId);
}
