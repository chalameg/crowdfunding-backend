package com.dxvalley.crowdfunding.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class CampaignLike {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long likeId;
    @OneToOne
    @JoinColumn(name = "campaign_Id")
    private Campaign campaign;
    @OneToOne
    @JoinColumn(name = "user_Id")
    private Users user;
}
