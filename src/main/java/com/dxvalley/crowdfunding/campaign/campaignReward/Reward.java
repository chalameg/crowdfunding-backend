package com.dxvalley.crowdfunding.campaign.campaignReward;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Double amountToCollect;
    private String deliveryTime;
    private String createdAt;
    private String editedAt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaignId", nullable = false)
    private Campaign campaign;
}
