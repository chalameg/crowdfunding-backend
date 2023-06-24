package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia;

import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.file.CampaignFile;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.file.CampaignFileService;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image.CampaignImage;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image.CampaignImageService;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.video.CampaignVideo;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.video.CampaignVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping({"/api/campaign-media"})
@RequiredArgsConstructor
public class CampaignMediaController {
    private final CampaignImageService campaignImageService;
    private final CampaignVideoService campaignVideoService;
    private final CampaignFileService campaignFileService;

    @PostMapping({"addImage/{campaignId}"})
    ResponseEntity<CampaignImage> addImage(@PathVariable Long campaignId, @RequestParam MultipartFile campaignImage) {
        return ResponseEntity.ok(this.campaignImageService.addImage(campaignId, campaignImage));
    }

    @PostMapping({"addVideo/{campaignId}"})
    ResponseEntity<CampaignVideo> uploadMedias(@PathVariable Long campaignId, @RequestParam String campaignVideoUrl) {
        return ResponseEntity.ok(this.campaignVideoService.addVideo(campaignId, campaignVideoUrl));
    }

    @PostMapping({"addFiles/{campaignId}"})
    ResponseEntity<List<CampaignFile>> uploadMedias(@PathVariable Long campaignId, @RequestParam List<MultipartFile> files) {
        return ResponseEntity.ok(this.campaignFileService.addFiles(campaignId, files));
    }
}
