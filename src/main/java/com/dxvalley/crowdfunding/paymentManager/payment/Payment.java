package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.userManager.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
    @Column(nullable = false)
    private String orderId;
    private double amount;
    private String currency;
    private String transactionId;
    private String payerFullName;
    private String paymentContactInfo;
    private String transactionOrderedDate;
    private String transactionCompletedDate;
    private Boolean isAnonymous;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentProcessor paymentProcessor;

    public Payment(String orderId, String transactionOrderedDate) {
        this.orderId = orderId;
        this.transactionOrderedDate = transactionOrderedDate;
    }

    public Payment(Long id, double amount, String payerFullName, String transactionCompletedDate, Boolean isAnonymous, String currency, PaymentStatus paymentStatus) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.payerFullName = payerFullName;
        this.transactionCompletedDate = transactionCompletedDate;
        this.isAnonymous = isAnonymous;
        this.paymentStatus = paymentStatus;
    }
}