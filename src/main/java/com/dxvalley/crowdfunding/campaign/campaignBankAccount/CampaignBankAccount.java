package com.dxvalley.crowdfunding.campaign.campaignBankAccount;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignBankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long campaignBankAccountId;
    private String bankAccount;
    @OneToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
}