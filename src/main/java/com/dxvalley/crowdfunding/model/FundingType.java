package com.dxvalley.crowdfunding.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class FundingType {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long fundingTypeId;

    private String name;
}
