package com.dxvalley.crowdfunding.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CampaignCategory {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long campaignCategoryId;
    private String name;
    private String description;

    public CampaignCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
