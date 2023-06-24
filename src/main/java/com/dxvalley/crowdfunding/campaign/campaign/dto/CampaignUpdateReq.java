package com.dxvalley.crowdfunding.campaign.campaign.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CampaignUpdateReq {
    private String title;
    private String shortDescription;
    private String city;
    private Double goalAmount;
    private String projectType;
    private String description;
    private String risks;
    private Short campaignDuration;
    private MultipartFile campaignImage;
    private String campaignVideoUrl;


}
