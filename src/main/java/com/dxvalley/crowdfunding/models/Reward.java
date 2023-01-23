package com.dxvalley.crowdfunding.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Reward {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long rewardId;

    private String title;
    private String description;
    private String amountToCollect;
    private String deliveryTime;

    @ManyToOne(fetch = FetchType.LAZY)
	private Campaign campaign;

}
