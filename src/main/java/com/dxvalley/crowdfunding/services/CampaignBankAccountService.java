package com.dxvalley.crowdfunding.services;

import com.dxvalley.crowdfunding.dto.BankAccountExistenceDTO;
import com.dxvalley.crowdfunding.models.CampaignBankAccount;

public interface CampaignBankAccountService {
    CampaignBankAccount addBankAccount(Long campaignId, String bankAccount);
    BankAccountExistenceDTO checkBankAccountExistence(String bankAccount) throws Exception;
    CampaignBankAccount getCampaignBankAccountByCampaignId(Long campaignId);
}
