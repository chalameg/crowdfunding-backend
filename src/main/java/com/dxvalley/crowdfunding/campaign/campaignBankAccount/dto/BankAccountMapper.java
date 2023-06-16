package com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.CampaignBankAccount;

import java.util.List;

public class BankAccountMapper {
    private BankAccountMapper() {
        // Private constructor to enforce non-instantiability
    }

    public static BankAccountRes toBankAccountDTO(CampaignBankAccount campaignBankAccount, List<Campaign> campaigns) {
        BankAccountRes bankAccountRes = new BankAccountRes();
        bankAccountRes.setId(campaignBankAccount.getId());
        bankAccountRes.setAccountNumber(campaignBankAccount.getAccountNumber());
        bankAccountRes.setAccountOwner(campaignBankAccount.getAccountOwner());
        bankAccountRes.setAddedAt(campaignBankAccount.getAddedAt());

        for (Campaign campaign : campaigns) {
            BankAccountRes.CampaignInfo campaignInfo = new BankAccountRes.CampaignInfo();
            campaignInfo.setCampaignOwner(campaign.getUser().getFullName());
            campaignInfo.setCampaignTitle(campaign.getTitle());

            bankAccountRes.addCampaign(campaignInfo);
        }

        return bankAccountRes;
    }
}
