package com.dxvalley.crowdfunding.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.dxvalley.crowdfunding.models.Reward;
import com.dxvalley.crowdfunding.repositories.RewardRepository;
import com.dxvalley.crowdfunding.services.RewardService;

@Service
public class RewardServiceImpl implements RewardService {
    private final RewardRepository rewardRepository;

    public RewardServiceImpl(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    @Override
    public Reward addReward(Reward reward) {
        return this.rewardRepository.save(reward);
    }

    @Override
    public Reward editReward(Reward reward) {
        return this.rewardRepository.save(reward);
    }

    @Override
    public List<Reward> findRewardsByCampaignId(Long campaignId) {
        return this.rewardRepository.findRewardsByCampaignCampaignId(campaignId);
    }

    @Override
    public Reward getRewardById(Long rewardId) {
        return this.rewardRepository.findRewardByRewardId(rewardId);
    }

    @Override
    public void deleteReward(Long rewardId) {
        rewardRepository.deleteById(rewardId);
    }
    
}
