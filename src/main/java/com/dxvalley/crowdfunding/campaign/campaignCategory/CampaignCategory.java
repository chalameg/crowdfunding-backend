package com.dxvalley.crowdfunding.campaign.campaignCategory;

import com.dxvalley.crowdfunding.campaign.campaignSubCategory.CampaignSubCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CampaignCategory {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long campaignCategoryId;
    private String name;
    private String description;
    @Transient
    private List<CampaignSubCategory> campaignSubCategories;
}
