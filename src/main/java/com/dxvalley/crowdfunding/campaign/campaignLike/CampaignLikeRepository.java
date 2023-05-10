package com.dxvalley.crowdfunding.campaign.campaignLike;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignLikeRepository extends JpaRepository<CampaignLike, Long> {
    CampaignLike findByCampaignCampaignIdAndUserUserId(Long campaignId, Long userId);
    List<CampaignLike> findByCampaignCampaignId(Long campaignId);
}
