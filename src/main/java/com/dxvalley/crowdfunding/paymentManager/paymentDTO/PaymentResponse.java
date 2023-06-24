package com.dxvalley.crowdfunding.paymentManager.paymentDTO;

import com.dxvalley.crowdfunding.paymentManager.payment.PaymentStatus;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentProcessor;
import lombok.Data;

@Data
public class PaymentResponse {
    private double amount;
    private String currency;
    private PaymentStatus paymentStatus;
    private String payerFullName;
    private String transactionCompletedDate;
    private PaymentProcessor paymentProcessor;
}
