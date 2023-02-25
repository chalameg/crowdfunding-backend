package com.dxvalley.crowdfunding.services;

import java.util.List;

import com.dxvalley.crowdfunding.models.Reward;

public interface RewardService {
    List<Reward> findRewardsByCampaignId(Long campaignId);

    Reward getRewardById(Long rewardId);

    Reward addReward(Long campaignId, Reward reward);

    Reward editReward(Long rewardId, Reward reward);

    void deleteReward(Long rewardId);
}
