package com.dxvalley.crowdfunding.campaign.campaignSharing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignSharingRepository extends JpaRepository<CampaignSharing, Long> {
    CampaignSharing findByCampaignIdAndUsernameAndSharingPlatform(Long campaignId, String username, String sharingPlatform);
    List<CampaignSharing> findByCampaignId(Long campaignId);
}
