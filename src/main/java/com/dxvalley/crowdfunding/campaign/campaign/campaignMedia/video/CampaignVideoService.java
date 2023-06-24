package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.video;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
@RequiredArgsConstructor
public class CampaignVideoService {
    private final CampaignVideoRepository campaignVideoRepository;
    private final CampaignUtils campaignUtils;
    private final DateTimeFormatter dateTimeFormatter;

    public CampaignVideo addVideo(Long campaignId, String campaignVideoUrl) {
        Campaign campaign = this.campaignUtils.utilGetCampaignById(campaignId);
        CampaignVideo campaignVideo = this.saveCampaignVideo(campaignVideoUrl);
        this.updateCampaignWithVideo(campaign, campaignVideo);
        return campaignVideo;
    }

    public CampaignVideo addVideo(Campaign campaign, String campaignVideoUrl) {
        CampaignVideo campaignVideo = this.saveCampaignVideo(campaignVideoUrl);
        this.updateCampaignWithVideo(campaign, campaignVideo);
        return campaignVideo;
    }

    public CampaignVideo saveCampaignVideo(String videoUrl) {
        CampaignVideo campaignVideo = CampaignVideo.builder().videoUrl(videoUrl).isPrimary(true).build();
        return (CampaignVideo) this.campaignVideoRepository.save(campaignVideo);
    }

    private void updateCampaignWithVideo(Campaign campaign, CampaignVideo campaignVideo) {
        campaign.setVideo(campaignVideo);
        campaign.setEditedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        this.campaignUtils.saveCampaign(campaign);
    }

    public void deleteCampaignVideo(Long videoId) {
        this.campaignVideoRepository.deleteById(videoId);
    }

}
