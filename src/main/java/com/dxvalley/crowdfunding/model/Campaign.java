package com.dxvalley.crowdfunding.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long campaignId;
    private String title;
    private String shortDescription;
    private String city;
    private String imageUrl;
    private String videoLink;
    private Double goalAmount;
    private String owner;
    private String projectType;
    @Enumerated(EnumType.STRING)
    private CampaignStage campaignStage;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String risks;
    private Short campaignDuration;
    private Double commissionRate;
    private Double totalAmountCollected;
    private Integer numberOfBackers;
    private Integer numberOfLikes;
    private Boolean isEnabled;
    private String createdAt;
    private String enabledAt;
    private String editedAt;
    private String expiredAt;
    private String dateDeleted;
    @OneToOne
    @JoinColumn(name = "fundingType_id")
    private FundingType fundingType;
    @OneToOne
    @JoinColumn(name = "campaignSubCategory_id")
    private CampaignSubCategory campaignSubCategory;
}
