package com.dxvalley.crowdfunding.campaign.campaignUpdate;

import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.CampaignUpdateDTO;
import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.CampaignUpdateResponseDTO;

import java.util.List;

public interface CampaignUpdateService {

    List<CampaignUpdateResponseDTO> getAllCampaignUpdates(Long campaignId);

    CampaignUpdateResponseDTO createCampaignUpdate(CampaignUpdateDTO campaignUpdateDTO);

    CampaignUpdateResponseDTO updateCampaignUpdate(Long id,CampaignUpdateDTO campaignUpdateDTO);

    void deleteCampaignUpdate(Long id);
}
