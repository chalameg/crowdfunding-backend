package com.dxvalley.crowdfunding.campaign.campaignSharing;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignSharing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long campaignId;
    private String username;

    private String sharingPlatform;

    private String sharingTime;

    private int shareCount;
}

