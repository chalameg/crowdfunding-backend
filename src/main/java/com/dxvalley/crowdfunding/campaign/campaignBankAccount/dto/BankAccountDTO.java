package com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto;

import lombok.Data;

@Data
public class BankAccountDTO {
    private Long campaignBankAccountId;
    private String bankAccount;
    private String campaignTitle;
}
