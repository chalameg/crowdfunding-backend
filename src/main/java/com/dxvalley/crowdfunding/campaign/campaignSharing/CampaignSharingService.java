package com.dxvalley.crowdfunding.campaign.campaignSharing;

import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignShareResponse;
import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignSharingDTO;

public interface CampaignSharingService {
    CampaignShareResponse getByCampaignId(Long campaignId);
    CampaignSharing addShareCampaign(CampaignSharingDTO campaignSharingDTO);
}
