package com.dxvalley.crowdfunding.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class CampaignBankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long campaignBankAccountId;
    private String bankAccount;
    @OneToOne(cascade = CascadeType.ALL)
    private Campaign campaign;

    public CampaignBankAccount(Long campaignBankAccountId, String bankAccount) {
        this.campaignBankAccountId = campaignBankAccountId;
        this.bankAccount = bankAccount;
    }
}
