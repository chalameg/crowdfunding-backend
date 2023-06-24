package com.dxvalley.crowdfunding.campaign.campaignFundingType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundingType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Short id;

    @Column(unique = true)
    private String name;

    public FundingType(String name) {
        this.name = name;
    }
}
