package com.dxvalley.crowdfunding.campaign.campaignSharing;

import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignShareCountResponse;
import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignShareResponse;
import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignSharingReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shareCampaign")
@RequiredArgsConstructor
public class CampaignSharingController {
    private final CampaignSharingService campaignSharingService;

    @GetMapping("/{campaignId}")
    public ResponseEntity<CampaignShareCountResponse> getByCampaignId(@PathVariable Long campaignId) {
        CampaignShareCountResponse campaignShareCountResponse = campaignSharingService.getByCampaignId(campaignId);
        return ResponseEntity.ok(campaignShareCountResponse);
    }

    @PostMapping
    public ResponseEntity<CampaignShareResponse> shareCampaign(@RequestBody @Valid CampaignSharingReq campaignSharingReq) {
        CampaignShareResponse campaignSharing = campaignSharingService.addShareCampaign(campaignSharingReq);
        return ResponseEntity.ok(campaignSharing);
    }
}
