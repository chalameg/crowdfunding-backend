package com.dxvalley.crowdfunding.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CampaignSubCategory {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long campaignSubCategoryId;
    private String name;
    private String description;
    
    @ManyToOne()
    @JoinColumn(name ="campaignCategoryId")
    private CampaignCategory campaignCategory;

    public CampaignSubCategory(Long campaignSubCategoryId, String name, String description) {
        this.campaignSubCategoryId = campaignSubCategoryId;
        this.name = name;
        this.description = description;
    }
}
