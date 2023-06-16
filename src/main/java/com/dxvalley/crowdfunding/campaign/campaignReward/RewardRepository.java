package com.dxvalley.crowdfunding.campaign.campaignReward;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

    List<Reward> findByCampaignId(Long campaignId);
}
