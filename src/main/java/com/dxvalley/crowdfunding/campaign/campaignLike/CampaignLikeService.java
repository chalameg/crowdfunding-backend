package com.dxvalley.crowdfunding.campaign.campaignLike;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignLikeService {
    private final CampaignLikeRepository campaignLikeRepository;

    public List<CampaignLike> findByUser(Long userId) {
        return campaignLikeRepository.findByUserUserId(userId);
    }

    public List<CampaignLike> findByCampaign(Long campaignId) {
        return campaignLikeRepository.findByCampaignId(campaignId);
    }
}
