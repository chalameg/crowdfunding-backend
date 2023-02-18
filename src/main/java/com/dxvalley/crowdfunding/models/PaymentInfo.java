package com.dxvalley.crowdfunding.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class PaymentInfo {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long paymentInfoId;
    private String paidAt;
    private Double amount;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "campaignId")
    private Campaign campaign;
    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;
    @Transient
    private String contributedBy;
    public PaymentInfo(Long paymentInfoId, String paidAt, Double amount, Users user) {
        this.paymentInfoId = paymentInfoId;
        this.paidAt = paidAt;
        this.amount = amount;
        this.contributedBy = user.getFullName();
    }
}
