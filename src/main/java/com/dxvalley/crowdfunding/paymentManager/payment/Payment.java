package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentProcessor;
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
    @Column(nullable = false, unique = true)
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
}