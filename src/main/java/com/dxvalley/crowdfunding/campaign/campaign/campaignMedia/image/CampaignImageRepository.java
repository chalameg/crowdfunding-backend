package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignImageRepository extends JpaRepository<CampaignImage, Long> {
}
