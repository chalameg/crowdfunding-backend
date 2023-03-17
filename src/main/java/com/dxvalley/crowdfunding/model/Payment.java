package com.dxvalley.crowdfunding.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue
    private Long paymentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
    @Column(nullable = false)
    private String orderId;
    private Double amount;
    private String currency;
    private String paymentStatus;
    private String transactionId;
    private String payerFullName;
    private String transactionOrderedDate;
    private String transactionCompletedDate;
    private Boolean isAnonymous;

    public Payment(String orderId, String transactionOrderedDate) {
        this.orderId = orderId;
        this.transactionOrderedDate = transactionOrderedDate;
    }

    public Payment(Long paymentId, Double amount, String payerFullName, String transactionCompletedDate, Boolean isAnonymous, String currency, String paymentStatus) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.currency = currency;
        this.payerFullName = payerFullName;
        this.transactionCompletedDate = transactionCompletedDate;
        this.isAnonymous = isAnonymous;
        this.paymentStatus = paymentStatus;
    }
}