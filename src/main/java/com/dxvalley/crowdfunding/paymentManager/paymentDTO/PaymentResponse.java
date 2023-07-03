package com.dxvalley.crowdfunding.paymentManager.paymentDTO;

import com.dxvalley.crowdfunding.paymentManager.payment.PaymentStatus;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentProcessor;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    private double amount;
    private String currency;
    private PaymentStatus paymentStatus;
    private String payerFullName;
    private String transactionCompletedDate;
    private PaymentProcessor paymentProcessor;
    private Long campaignId;
    private String campaignTitle;
}
