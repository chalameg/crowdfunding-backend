package com.dxvalley.crowdfunding.services;

import java.util.List;

import com.dxvalley.crowdfunding.models.Reward;


public interface RewardService {
    Reward addReward (Reward reward);
    Reward editReward (Reward reward);
    List<Reward> findRewardsByCampaignId(Long campaignId);
    Reward getRewardById(Long rewardId);
    void deleteReward( Long rewardId);
}
