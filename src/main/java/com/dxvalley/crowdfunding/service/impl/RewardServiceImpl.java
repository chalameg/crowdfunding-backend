package com.dxvalley.crowdfunding.service.impl;

import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.model.Campaign;
import com.dxvalley.crowdfunding.model.Reward;
import com.dxvalley.crowdfunding.repository.CampaignRepository;
import com.dxvalley.crowdfunding.repository.RewardRepository;
import com.dxvalley.crowdfunding.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
//TODO: update current impl
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
        if (rewards.isEmpty())
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
