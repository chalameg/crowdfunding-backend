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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long paymentId;

    private String payerFullName;
    private String transactionOrderDate;
    private String orderId;
    private String paymentStatus;
    private Double amount;
    private Boolean isAnonymous;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    //    @ManyToOne
//    @JoinColumn(name = "userId")
//    private Users user;
    @OneToOne
    @JoinColumn(name = "userId")
    Users user;

    public Payment(Long paymentId, String payerFullName, String transactionOrderDate, String orderId,
                   String paymentStatus, Double amount, Boolean isAnonymous) {
        this.paymentId = paymentId;
        this.payerFullName = payerFullName;
        this.transactionOrderDate = transactionOrderDate;
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.isAnonymous = isAnonymous;
    }

}
