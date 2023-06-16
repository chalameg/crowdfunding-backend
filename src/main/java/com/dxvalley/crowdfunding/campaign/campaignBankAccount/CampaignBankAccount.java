package com.dxvalley.crowdfunding.campaign.campaignBankAccount;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    private String accountNumber;
    @Column(nullable = false)
    private String accountOwner;
    @Column(nullable = false)
    private String addedAt;
}
