package com.dxvalley.crowdfunding.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
