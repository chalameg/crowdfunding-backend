package com.dxvalley.crowdfunding.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long companyId;

    private String title;
    private String shortDescription;
    private String city;
    private String imageUrl;
    private String videoLink;
    private String goalAmount;
    private String campaignDuration;

    @OneToMany(targetEntity = Company.class, cascade = CascadeType.ALL)
    private List<Company> companies;
}
