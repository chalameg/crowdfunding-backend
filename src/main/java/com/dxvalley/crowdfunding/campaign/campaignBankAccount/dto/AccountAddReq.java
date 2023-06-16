package com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountAddReq {
    @NotBlank
    @Pattern(regexp = "^[0-9]{13}$", message = "Account number must be 13 digits")
    private String accountNumber;

    @NotBlank
    private String accountOwner;

    @NotNull
    private Long campaignId;
}
