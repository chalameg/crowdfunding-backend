package com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BankAccountRes {
    private Long id;
    private String accountNumber;
    private String accountOwner;
    private String addedAt;
    private List<CampaignInfo> campaigns;

    public void addCampaign(CampaignInfo campaignInfo) {
        if (campaigns == null) {
            campaigns = new ArrayList<>();
        }
        campaigns.add(campaignInfo);
    }

    @Data
    public static class CampaignInfo {
        private String campaignTitle;
        private String campaignOwner;
    }
}

