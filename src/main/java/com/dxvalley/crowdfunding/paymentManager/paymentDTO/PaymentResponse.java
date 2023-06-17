package com.dxvalley.crowdfunding.payment.paymentDTO;

import com.dxvalley.crowdfunding.payment.PaymentProcessor;
import com.dxvalley.crowdfunding.payment.PaymentStatus;
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
