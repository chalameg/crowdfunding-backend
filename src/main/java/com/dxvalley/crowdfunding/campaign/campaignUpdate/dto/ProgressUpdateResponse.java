package com.dxvalley.crowdfunding.campaign.campaignUpdate.dto;

import lombok.Data;

@Data
public class ProgressUpdateResponse {
    private Long id;
    private String title;
    private String createdAt;
    private String updatedAt;
    private String description;
    private String authorName;
}