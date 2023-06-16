package com.dxvalley.crowdfunding.campaign.campaignReward;

import com.dxvalley.crowdfunding.campaign.campaignReward.dto.RewardRequest;
import com.dxvalley.crowdfunding.campaign.campaignReward.dto.RewardResponse;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RewardService {
    List<RewardResponse> getByCampaign(Long campaignId);

    RewardResponse getRewardById(Long rewardId);

    RewardResponse addReward(RewardRequest rewardUpdate);

    RewardResponse editReward(Long rewardId, RewardRequest rewardRequest);

    ResponseEntity<ApiResponse> deleteReward(Long rewardId);
}
