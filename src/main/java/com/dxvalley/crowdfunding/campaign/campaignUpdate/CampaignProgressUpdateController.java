package com.dxvalley.crowdfunding.campaign.campaignUpdate;

import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.ProgressUpdateReq;
import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.ProgressUpdateResponse;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaign-updates")
@RequiredArgsConstructor
public class CampaignProgressUpdateController {
    private final CampaignProgressUpdateService campaignProgressUpdateService;

    @GetMapping("/byCampaign/{campaignId}")
    public ResponseEntity<List<ProgressUpdateResponse>> getAllCampaignUpdates(@PathVariable Long campaignId) {
        List<ProgressUpdateResponse> campaignUpdates = campaignProgressUpdateService.getAllCampaignUpdates(campaignId);
        return ResponseEntity.ok(campaignUpdates);
    }

    @PostMapping
    public ResponseEntity<ProgressUpdateResponse> createCampaignUpdate(@RequestBody @Valid ProgressUpdateReq progressUpdateReq) {
        ProgressUpdateResponse progressUpdateResponse = campaignProgressUpdateService.createCampaignUpdate(progressUpdateReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(progressUpdateResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgressUpdateResponse> updateCampaignUpdate(@PathVariable Long id, @RequestBody ProgressUpdateReq progressUpdateReq) {
        ProgressUpdateResponse progressUpdateResponse = campaignProgressUpdateService.updateCampaignUpdate(id, progressUpdateReq);
        return ResponseEntity.ok(progressUpdateResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCampaignUpdate(@PathVariable Long id) {
        return campaignProgressUpdateService.deleteCampaignUpdate(id);
    }
}
