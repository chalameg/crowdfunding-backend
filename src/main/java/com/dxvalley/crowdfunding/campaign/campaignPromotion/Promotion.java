package com.dxvalley.crowdfunding.campaign.campaignPromotion;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long promotionId;
    private String promotionLink;
    private String description;
    @ManyToOne
    private Campaign campaign;

    public Promotion(Long promotionId, String promotionLink, String description) {
        this.promotionId = promotionId;
        this.promotionLink = promotionLink;
        this.description = description;
    }
}
