package com.dxvalley.crowdfunding.campaign.campaignReward;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignReward.dto.RewardMapper;
import com.dxvalley.crowdfunding.campaign.campaignReward.dto.RewardRequest;
import com.dxvalley.crowdfunding.campaign.campaignReward.dto.RewardResponse;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final CampaignUtils campaignUtils;
    private final DateTimeFormatter dateTimeFormatter;

    @Override
    public RewardResponse addReward(RewardRequest rewardRequest) {
        Campaign campaign = campaignUtils.utilGetCampaignById(rewardRequest.getCampaignId());

        Reward reward = new Reward();

        reward.setTitle(rewardRequest.getTitle());
        reward.setDescription(rewardRequest.getDescription());
        reward.setAmountToCollect(rewardRequest.getAmountToCollect());
        reward.setDeliveryTime(LocalDateTime.now().format(dateTimeFormatter));
        reward.setCampaign(campaign);
        reward.setCreatedAt(LocalDateTime.now().format(dateTimeFormatter));

        Reward savedReward = rewardRepository.save(reward);

        return RewardMapper.toRewardResponse(savedReward);
    }

    @Override
    public RewardResponse editReward(Long rewardId, RewardRequest rewardUpdate) {
        Reward reward = utilGetRewardById(rewardId);

        if (rewardUpdate.getTitle() != null)
            reward.setTitle(rewardUpdate.getTitle());

        if (rewardUpdate.getDescription() != null)
            reward.setDescription(rewardUpdate.getDescription());

        if (rewardUpdate.getAmountToCollect() != null)
            reward.setAmountToCollect(rewardUpdate.getAmountToCollect());

        if (rewardUpdate.getDeliveryTime() != null)
            reward.setDeliveryTime(rewardUpdate.getDeliveryTime());

        reward.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

        Reward savedReward = rewardRepository.save(reward);

        return RewardMapper.toRewardResponse(savedReward);
    }


    @Override
    public List<RewardResponse> getByCampaign(Long campaignId) {
        List<Reward> rewards = rewardRepository.findByCampaignId(campaignId);

        return rewards.stream().map(RewardMapper::toRewardResponse).toList();
    }

    @Override
    public RewardResponse getRewardById(Long rewardId) {
        Reward reward = utilGetRewardById(rewardId);
        return RewardMapper.toRewardResponse(reward);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteReward(Long rewardId) {
        utilGetRewardById(rewardId);
        rewardRepository.deleteById(rewardId);

        return ApiResponse.success("Deleted Successfully");
    }


    private Reward utilGetRewardById(Long rewardId) {
        return rewardRepository.findById(rewardId).orElseThrow(
                () -> new ResourceNotFoundException("There is no Reward with this Id"));
    }

}
