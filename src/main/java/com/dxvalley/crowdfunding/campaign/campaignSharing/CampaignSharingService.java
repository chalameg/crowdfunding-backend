package com.dxvalley.crowdfunding.campaign.campaignSharing;

import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignShareCountResponse;
import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignShareResponse;
import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignSharingReq;

public interface CampaignSharingService {
    CampaignShareCountResponse getByCampaignId(Long campaignId);

    CampaignShareResponse addShareCampaign(CampaignSharingReq campaignSharingReq);
}
