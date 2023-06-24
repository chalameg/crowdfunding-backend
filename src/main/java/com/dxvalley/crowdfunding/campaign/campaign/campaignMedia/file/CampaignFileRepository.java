package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignFileRepository extends JpaRepository<CampaignFile, Long> {
}
