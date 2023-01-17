package com.dxvalley.crowdfunding.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
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

    //campaign reward
    @OneToOne(cascade = CascadeType.ALL)
    private Reward reward;

    //campaign payment
    @OneToOne(cascade = CascadeType.ALL)
    private Payment payment;

    //campaign Promotion
    @OneToOne(cascade = CascadeType.ALL)
    private Promotion promotion;

    //campaign FundingType
    @OneToOne(cascade = CascadeType.ALL)
    private FundingType fundingType;

    //campaign CampaignCategory
    @OneToOne(cascade = CascadeType.ALL)
    private CampaignCategory campaignCategory;



    @Column(nullable = false, length = 512)
    @NotBlank(message = "Campaign title cannot be empty!")
    private String title;
    
    private String shortDescription;
    private String city;
    private String imageUrl;
    private String videoLink;
    private String goalAmount;
    private String owner;
    private String campaignDuration;

   @Column(columnDefinition="TEXT")
    private String decription;

   @Column(columnDefinition="TEXT")
    private String risks;
    private String projectType;
    private Boolean isEnabled;
    @JsonFormat(pattern="yyyy-MM-dd",shape = Shape.STRING)
    @Column(name="date_created")
    private String dateCreated;
    @JsonFormat(pattern="yyyy-MM-dd",shape = Shape.STRING)
    @Column(name="date_deleted")
    private String dateDeleted;

    @Transient
    private List<Collaborator> collaborators;

}
