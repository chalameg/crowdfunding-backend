package com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BankAccountDTO {
    private Long campaignBankAccountId;
    private String bankAccount;
    private String campaignTitle;
    private String message;

    public BankAccountDTO(String message) {
        this.message = message;
    }
}
