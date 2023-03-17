package com.dxvalley.crowdfunding.repository;

import com.dxvalley.crowdfunding.model.CampaignLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignLikeRepository extends JpaRepository<CampaignLike, Long> {
    CampaignLike findByCampaignCampaignIdAndUserUserId(Long campaignId, Long userId);
}
