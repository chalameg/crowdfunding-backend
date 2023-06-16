package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long imageId;
    private String imageUrl;
    private boolean isPrimary;
}

