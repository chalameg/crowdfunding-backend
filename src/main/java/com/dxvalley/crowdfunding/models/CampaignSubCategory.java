package com.dxvalley.crowdfunding.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class CampaignSubCategory {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long campaignSubCategoryId;
    private String name;
    private String description;
    @ManyToOne
    private CampaignCategory category;
}
