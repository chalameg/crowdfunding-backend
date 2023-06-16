package com.dxvalley.crowdfunding.campaign.campaignBankAccount;

import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.AccountAddReq;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.AccountExistenceRes;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountRes;

public interface CampaignBankAccountService {
    BankAccountRes getByAccountNumber(String accountNumber);

    CampaignBankAccount addBankAccount(AccountAddReq accountAddReq);

    AccountExistenceRes checkBankAccountExistence(String bankAccount);

}
