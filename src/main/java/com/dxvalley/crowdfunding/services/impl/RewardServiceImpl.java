package com.dxvalley.crowdfunding.services.impl;

import java.util.List;

import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.repositories.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dxvalley.crowdfunding.models.Reward;
import com.dxvalley.crowdfunding.repositories.RewardRepository;
import com.dxvalley.crowdfunding.services.RewardService;

@Service
public class RewardServiceImpl implements RewardService {
    @Autowired
    private RewardRepository rewardRepository;
    @Autowired
    CampaignRepository campaignRepository;

    @Override
    public Reward addReward(Long campaignId, Reward reward) {
        Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId).orElseThrow(
                () -> new ResourceNotFoundException("There is no campaign with this ID.")
        );
        reward.setCampaign(campaign);
        return rewardRepository.save(reward);
    }

    @Override
    public Reward editReward(Long rewardId, Reward tempReward) {
        Reward reward = getRewardById(rewardId);

        reward.setTitle(tempReward.getTitle() != null ? tempReward.getTitle() : reward.getTitle());
        reward.setDescription(tempReward.getDescription() != null ? tempReward.getDescription() : reward.getDescription());
        reward.setAmountToCollect(tempReward.getAmountToCollect() != null ? tempReward.getAmountToCollect() : reward.getAmountToCollect());
        reward.setDeliveryTime(tempReward.getDeliveryTime() != null ? tempReward.getDeliveryTime() : reward.getDeliveryTime());

        return rewardRepository.save(reward);
    }

    @Override
    public List<Reward> findRewardsByCampaignId(Long campaignId) {
        var rewards = rewardRepository.findRewardsByCampaignId(campaignId);
        if (rewards.size() == 0)
            throw new ResourceNotFoundException("Currently, There is no reward for this Campaign");
        return rewards;
    }

    @Override
    public Reward getRewardById(Long rewardId) {
        Reward reward = rewardRepository.findRewardByRewardId(rewardId).orElseThrow(
                () -> new ResourceNotFoundException("There is no Reward with this Id")
        );
        return reward;
    }

    @Override
    public void deleteReward(Long rewardId) {
        var reward = getRewardById(rewardId);
        rewardRepository.delete(reward);
    }

}
