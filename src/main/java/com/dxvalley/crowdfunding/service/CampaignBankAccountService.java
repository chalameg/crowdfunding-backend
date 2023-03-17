package com.dxvalley.crowdfunding.service;

import com.dxvalley.crowdfunding.dto.BankAccountExistenceDTO;
import com.dxvalley.crowdfunding.model.CampaignBankAccount;

public interface CampaignBankAccountService {
    CampaignBankAccount addBankAccount(Long campaignId, String bankAccount);
    BankAccountExistenceDTO checkBankAccountExistence(String bankAccount) throws Exception;
    CampaignBankAccount getCampaignBankAccountByCampaignId(Long campaignId);
}
