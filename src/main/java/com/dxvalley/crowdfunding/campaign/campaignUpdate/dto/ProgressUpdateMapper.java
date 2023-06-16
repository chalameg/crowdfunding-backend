package com.dxvalley.crowdfunding.campaign.campaignUpdate.dto;

import com.dxvalley.crowdfunding.campaign.campaignUpdate.CampaignProgressUpdate;

public class ProgressUpdateMapper {
    public static ProgressUpdateResponse toResponseDTO(CampaignProgressUpdate campaignProgressUpdate) {
        ProgressUpdateResponse responseDTO = new ProgressUpdateResponse();
        responseDTO.setId(campaignProgressUpdate.getId());
        responseDTO.setTitle(campaignProgressUpdate.getTitle());
        responseDTO.setCreatedAt(campaignProgressUpdate.getCreatedAt());
        responseDTO.setUpdatedAt(campaignProgressUpdate.getUpdatedAt());
        responseDTO.setDescription(campaignProgressUpdate.getDescription());
        responseDTO.setAuthorName(campaignProgressUpdate.getAuthor().getFullName());
        return responseDTO;
    }
}
