package com.dxvalley.crowdfunding.campaign.campaignReward.dto;

import com.dxvalley.crowdfunding.campaign.campaignReward.Reward;

public class RewardMapper {

    public static RewardResponse toRewardResponse(Reward reward) {
        return RewardResponse.builder()
                .id(reward.getId())
                .title(reward.getTitle())
                .description(reward.getDescription())
                .amountToCollect(reward.getAmountToCollect())
                .deliveryTime(reward.getDeliveryTime())
                .createdAt(reward.getCreatedAt())
                .editedAt(reward.getEditedAt())
                .build();
    }
}
