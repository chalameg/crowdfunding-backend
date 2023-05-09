package com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto;

import com.dxvalley.crowdfunding.campaign.campaignBankAccount.CampaignBankAccount;
import org.springframework.stereotype.Component;

@Component
public class BankAccountMapper {
    public static BankAccountDTO toBankAccountDTO(CampaignBankAccount campaignBankAccount) {
        BankAccountDTO bankAccountDTO = new BankAccountDTO();
        bankAccountDTO.setCampaignBankAccountId(campaignBankAccount.getCampaignBankAccountId());
        bankAccountDTO.setBankAccount(campaignBankAccount.getBankAccount());
        bankAccountDTO.setCampaignTitle(campaignBankAccount.getCampaign().getTitle());

        return bankAccountDTO;
    }
}
