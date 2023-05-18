package com.dxvalley.crowdfunding.payment.paymentDTO;

import com.dxvalley.crowdfunding.payment.Payment;

public class PaymentMapper {
    public static PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setAmount(payment.getAmount());
        paymentResponse.setCurrency(payment.getCurrency());
        paymentResponse.setPaymentStatus(payment.getPaymentStatus());
        paymentResponse.setPayerFullName(payment.getIsAnonymous() ? "Anonymous" : payment.getPayerFullName());
        paymentResponse.setTransactionCompletedDate(payment.getTransactionCompletedDate());
        paymentResponse.setPaymentProcessor(payment.getPaymentProcessor().name());

        return paymentResponse;
    }
}