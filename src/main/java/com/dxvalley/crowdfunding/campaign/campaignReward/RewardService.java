package com.dxvalley.crowdfunding.campaign.campaignReward;

import java.util.List;

public interface RewardService {
    List<Reward> findRewardsByCampaignId(Long campaignId);

    Reward getRewardById(Long rewardId);

    Reward addReward(Long campaignId, Reward reward);

    Reward editReward(Long rewardId, Reward reward);

    void deleteReward(Long rewardId);
}
