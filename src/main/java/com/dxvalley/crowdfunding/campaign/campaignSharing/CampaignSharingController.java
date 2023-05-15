package com.dxvalley.crowdfunding.campaign.campaignSharing;

import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignShareResponse;
import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignSharingDTO;
import com.dxvalley.crowdfunding.utils.ApiResponse;
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
    public ResponseEntity<?> getByCampaignId(@PathVariable Long campaignId) {
        CampaignShareResponse campaignShareResponse = campaignSharingService.getByCampaignId(campaignId);
        return ApiResponse.success(campaignShareResponse);
    }

    @PostMapping
    public ResponseEntity<?> shareCampaign(@RequestBody @Valid CampaignSharingDTO campaignSharingDTO) {
        CampaignSharing campaignSharing = campaignSharingService.addShareCampaign(campaignSharingDTO);
        return ApiResponse.created(campaignSharing);
    }
}
