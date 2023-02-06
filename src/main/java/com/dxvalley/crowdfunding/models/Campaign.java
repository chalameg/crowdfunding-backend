package com.dxvalley.crowdfunding.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

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
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long campaignId;
    @OneToOne(cascade = CascadeType.ALL)
    private FundingType fundingType;
    @OneToOne(cascade = CascadeType.ALL)
    private CampaignSubCategory campaignSubCategory;

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
    @Column(columnDefinition="TEXT")
    private String description;
    @Column(columnDefinition="TEXT")
    private String risks;
    private Short campaignDuration;
    private Boolean isEnabled;
    private String createdAt;
    private String enabledAt;
    private String editedAt;
    private String expiredAt;
    private String dateDeleted;


    @Transient
    private List<Collaborator> collaborators;
    @Transient
    private Payment payment;
    @Transient
    private List<Reward> rewards;
    @Transient
    private List<Promotion> promotions;
    @Transient
    private String ownerName;
    @Transient
    private Integer numberOfCampaigns;

    public Campaign(Long campaignId, String title, String shortDescription, String city, String imageUrl,
                    Double goalAmount,Short campaignDuration, String projectType, CampaignStage campaignStage) {
        this.campaignId = campaignId;
        this.title = title;
        this.shortDescription = shortDescription;
        this.city = city;
        this.imageUrl = imageUrl;
        this.goalAmount = goalAmount;
        this.campaignDuration = campaignDuration;
        this.projectType = projectType;
        this.campaignStage = campaignStage;
    }
}
