package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignVideoRepository extends JpaRepository<CampaignVideo, Long> {
}

