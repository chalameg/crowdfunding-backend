package com.dxvalley.crowdfunding.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

    //campaign story
    @OneToOne(cascade = CascadeType.ALL)
    private Story story;
    
    //campaign reward
    @OneToOne(cascade = CascadeType.ALL)
    private Reward reward;

    //campaign payment
    @OneToOne(cascade = CascadeType.ALL)
    private Payment payment;

    //campaign Promotion
    @OneToOne(cascade = CascadeType.ALL)
    private Promotion promotion;

    @Column(nullable = false, length = 512)
    @NotBlank(message = "Campaign title cannot be empty!")
    private String title;
    private String shortDescription;
    private String city;
    private String imageUrl;
    private String videoLink;
    private String goalAmount;
    private String campaignDuration;
    private Boolean isEnabled;

}
