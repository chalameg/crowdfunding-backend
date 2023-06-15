package com.dxvalley.crowdfunding.campaign.campaignCategory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryReq {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;
    
}

