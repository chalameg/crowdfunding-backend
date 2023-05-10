package com.dxvalley.crowdfunding.campaign.campaignBankAccount;

import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountDTO;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountExistenceDTO;

public interface CampaignBankAccountService {
    BankAccountDTO addBankAccount(Long campaignId, String bankAccount);
    BankAccountExistenceDTO checkBankAccountExistence(String bankAccount);
    BankAccountDTO getCampaignBankAccountByCampaignId(Long campaignId);
}
