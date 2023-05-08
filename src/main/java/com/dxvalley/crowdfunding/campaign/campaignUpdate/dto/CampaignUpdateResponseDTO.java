package com.dxvalley.crowdfunding.campaign.campaignUpdate.dto;

import lombok.Data;

@Data
public class CampaignUpdateResponseDTO {
    private Long id;
    private String title;
    private String time;
    private String description;
    private String authorName;
}