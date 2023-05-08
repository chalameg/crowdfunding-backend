package com.dxvalley.crowdfunding.campaign.campaignUpdate;

import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.CampaignUpdateDTO;
import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.CampaignUpdateResponseDTO;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campaign-updates")
@RequiredArgsConstructor
public class CampaignUpdateController {
    private final CampaignUpdateService campaignUpdateService;

    @GetMapping("/{campaignId}")
    public ResponseEntity<?> getAllCampaignUpdates(@PathVariable Long campaignId) {
        List<CampaignUpdateResponseDTO> campaignUpdates = campaignUpdateService.getAllCampaignUpdates(campaignId);
        return ApiResponse.success(campaignUpdates);
    }

    @PostMapping
    public ResponseEntity<?> createCampaignUpdate(@RequestBody @Valid CampaignUpdateDTO campaignUpdateDTO) {
        CampaignUpdateResponseDTO campaignUpdateResponseDTO = campaignUpdateService.createCampaignUpdate(campaignUpdateDTO);
        return ApiResponse.created(campaignUpdateResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCampaignUpdate(@PathVariable Long id, @RequestBody CampaignUpdateDTO campaignUpdateDTO) {
        CampaignUpdateResponseDTO campaignUpdateResponseDTO = campaignUpdateService.updateCampaignUpdate(id,campaignUpdateDTO);
        return ApiResponse.success(campaignUpdateResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCampaignUpdate(@PathVariable Long id) {
        campaignUpdateService.deleteCampaignUpdate(id);
        return ApiResponse.success("campaign update is deleted successfully.");
    }
}
