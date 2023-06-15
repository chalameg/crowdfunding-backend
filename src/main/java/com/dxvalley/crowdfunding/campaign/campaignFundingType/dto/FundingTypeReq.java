package com.dxvalley.crowdfunding.campaign.campaignFundingType.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FundingTypeReq {
    @NotBlank
    @Pattern(regexp = "(?i)^(donation|equity|reward)$", message = "Invalid funding type")
    private String name;
}
