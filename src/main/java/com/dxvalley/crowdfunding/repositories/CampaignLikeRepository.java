package com.dxvalley.crowdfunding.repositories;

import com.dxvalley.crowdfunding.models.CampaignLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignLikeRepository extends JpaRepository<CampaignLike, Long> {
    CampaignLike findByCampaignCampaignIdAndUserUserId(Long campaignId, Long userId);
}
