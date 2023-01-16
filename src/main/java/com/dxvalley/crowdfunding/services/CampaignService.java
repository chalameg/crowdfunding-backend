package com.dxvalley.crowdfunding.services;

import java.util.List;

import com.dxvalley.crowdfunding.models.Campaign;


public interface CampaignService {
    Campaign addCampaign (Campaign campaign);
    Campaign editCampaign (Campaign campaign);
    List<Campaign> getCampaigns ();
    Campaign getCampaignById(Long campaignId);
    void deleteCampaign( Long campaignId);
    List<Campaign> findCampaignsByOwner(String owner);
}
