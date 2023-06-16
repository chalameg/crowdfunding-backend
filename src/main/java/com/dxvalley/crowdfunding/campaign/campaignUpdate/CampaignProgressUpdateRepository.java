package com.dxvalley.crowdfunding.campaign.campaignUpdate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignProgressUpdateRepository extends JpaRepository<CampaignProgressUpdate, Long> {
    List<CampaignProgressUpdate> findByCampaignId(Long campaignId);
}
