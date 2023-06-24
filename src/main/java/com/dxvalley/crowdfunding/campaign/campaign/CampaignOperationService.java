package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignAddReq;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignUpdateReq;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLikeReq;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface CampaignOperationService {
    Campaign addCampaign(CampaignAddReq campaignAddRequestDto);

    CampaignDTO editCampaign(Long campaignId, CampaignUpdateReq campaignUpdateReq);

    CampaignDTO submitCampaign(Long campaignId);

    CampaignDTO withdrawCampaign(Long campaignId);

    CampaignDTO pauseCampaign(Long campaignId);

    CampaignDTO resumeCampaign(Long campaignId);

    ResponseEntity<ApiResponse> likeCampaign(CampaignLikeReq campaignLikeReq);

    ResponseEntity<ApiResponse> deleteCampaign(Long campaignId);
}
