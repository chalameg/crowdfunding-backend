package com.dxvalley.crowdfunding.campaign.campaignReward;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    Optional<Reward> findRewardByRewardId(Long rewardId);

    @Query("SELECT new Reward(r.rewardId, r.title, r.description," +
            " r.amountToCollect, r.deliveryTime)" +
            " from Reward as r WHERE r.campaign.campaignId = :campaignId")
    List<Reward> findRewardsByCampaignId(Long campaignId);
}
