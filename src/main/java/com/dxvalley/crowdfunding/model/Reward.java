package com.dxvalley.crowdfunding.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
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
    @JsonFormat(pattern="yyyy-MM-dd",shape = JsonFormat.Shape.STRING)
    private String deliveryTime;

    @ManyToOne
	private Campaign campaign;

    public Reward(Long rewardId, String title, String description, String amountToCollect, String deliveryTime) {
        this.rewardId = rewardId;
        this.title = title;
        this.description = description;
        this.amountToCollect = amountToCollect;
        this.deliveryTime = deliveryTime;
    }
}
