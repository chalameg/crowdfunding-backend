package com.dxvalley.crowdfunding.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Collaborator {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long collaboratorId;
    private boolean isCampaignCreator;

    @ManyToOne
    @JoinColumn(name = "userId") 
    Users users; 
 
    @ManyToOne 
    @JoinColumn(name = "campaignId") 
    Campaign campaign; 
 
}
