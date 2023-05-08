package com.dxvalley.crowdfunding.campaign.campaignUpdate.dto;

import com.dxvalley.crowdfunding.campaign.campaignUpdate.CampaignUpdate;
import org.springframework.stereotype.Component;
@Component
public class CampaignUpdateMapper {
    public static CampaignUpdateResponseDTO toResponseDTO(CampaignUpdate campaignUpdate) {
        CampaignUpdateResponseDTO responseDTO = new CampaignUpdateResponseDTO();
        responseDTO.setId(campaignUpdate.getId());
        responseDTO.setTitle(campaignUpdate.getTitle());
        responseDTO.setTime(campaignUpdate.getDateTime());
        responseDTO.setDescription(campaignUpdate.getDescription());
        responseDTO.setAuthorName(campaignUpdate.getAuthor().getFullName());
        return responseDTO;
    }
}
