package com.dxvalley.crowdfunding.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long promotionId;
    private String promotionLink;
    private String description;
    @ManyToOne
    private Campaign campaign;
    
}
