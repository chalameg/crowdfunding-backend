package com.dxvalley.crowdfunding.campaign.campaignUpdate;

import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.ProgressUpdateReq;
import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.ProgressUpdateResponse;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CampaignProgressUpdateService {

    List<ProgressUpdateResponse> getAllCampaignUpdates(Long campaignId);

    ProgressUpdateResponse createCampaignUpdate(ProgressUpdateReq progressUpdateReq);

    ProgressUpdateResponse updateCampaignUpdate(Long id, ProgressUpdateReq progressUpdateReq);

    ResponseEntity<ApiResponse> deleteCampaignUpdate(Long id);
}
