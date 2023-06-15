package com.dxvalley.crowdfunding.campaign.campaignSubCategory;

import com.dxvalley.crowdfunding.campaign.campaignCategory.CampaignCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignSubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Short id;
    private String name;
    private String description;

    @ManyToOne()
    @JoinColumn(name = "categoryId")
    private CampaignCategory campaignCategory;
}
