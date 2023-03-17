package com.dxvalley.crowdfunding.service;

import com.dxvalley.crowdfunding.model.Reward;

import java.util.List;

public interface RewardService {
    List<Reward> findRewardsByCampaignId(Long campaignId);

    Reward getRewardById(Long rewardId);

    Reward addReward(Long campaignId, Reward reward);

    Reward editReward(Long rewardId, Reward reward);

    void deleteReward(Long rewardId);
}
