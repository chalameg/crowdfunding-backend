package com.dxvalley.crowdfunding.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long paymentId;
    private String bankAccount;
    @OneToOne(cascade = CascadeType.ALL)
    private Campaign campaign;

    public Payment(Long paymentId, String bankAccount) {
        this.paymentId = paymentId;
        this.bankAccount = bankAccount;
    }
}
