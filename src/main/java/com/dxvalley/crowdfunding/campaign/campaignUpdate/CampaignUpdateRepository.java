package com.dxvalley.crowdfunding.campaign.campaignUpdate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignUpdateRepository extends JpaRepository<CampaignUpdate, Long> {
    List<CampaignUpdate> findCampaignUpdatesByCampaignCampaignId(Long campaignId);
}
