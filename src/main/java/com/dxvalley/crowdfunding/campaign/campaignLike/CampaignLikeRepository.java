package com.dxvalley.crowdfunding.campaign.campaignLike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignLikeRepository extends JpaRepository<CampaignLike, Long> {
    CampaignLike findByCampaignIdAndUserUserId(Long campaignId, Long userId);

    List<CampaignLike> findByCampaignId(Long campaignId);

    List<CampaignLike> findByUserUserId(Long userId);
}
