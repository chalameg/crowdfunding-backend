package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dxvalley.crowdfunding.models.Reward;
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
