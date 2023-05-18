package com.dxvalley.crowdfunding.payment.paymentDTO;

import lombok.Data;

@Data
public class PaymentResponse {
    private double amount;
    private String currency;
    private String paymentStatus;
    private String payerFullName;
    private String transactionCompletedDate;
    private String paymentProcessor;
}
