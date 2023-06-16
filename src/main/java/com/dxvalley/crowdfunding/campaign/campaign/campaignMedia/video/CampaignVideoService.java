package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.video;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CampaignVideoService {
    private final CampaignVideoRepository campaignVideoRepository;

    public CampaignVideo saveCampaignVideo(String videoUrl) {
        CampaignVideo campaignVideo = CampaignVideo.builder()
                .videoUrl(videoUrl)
                .isPrimary(true)
                .build();
        return campaignVideoRepository.save(campaignVideo);
    }

    public void deleteCampaignVideo(Long videoId) {
        campaignVideoRepository.deleteById(videoId);
    }

    public Optional<CampaignVideo> getCampaignVideoById(Long videoId) {
        return campaignVideoRepository.findById(videoId);
    }

}
