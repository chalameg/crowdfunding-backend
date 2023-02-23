package com.dxvalley.crowdfunding.repositories;

import com.dxvalley.crowdfunding.models.CampaignBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CampaignBankAccountRepository extends JpaRepository<CampaignBankAccount, Long>{
    @Query("SELECT new CampaignBankAccount(c.campaignBankAccountId, c.bankAccount)" +
            " from CampaignBankAccount as c WHERE c.campaign.campaignId = :campaignId")
    CampaignBankAccount findCampaignBankAccountByCampaignId(Long campaignId);
}

