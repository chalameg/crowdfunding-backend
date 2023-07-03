package com.dxvalley.crowdfunding.paymentManager.paymentDTO;

import com.dxvalley.crowdfunding.paymentManager.payment.Payment;

public class PaymentMapper {
    public PaymentMapper() {
    }

    public static PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setAmount(payment.getAmount());
        paymentResponse.setCurrency(payment.getCurrency());
        paymentResponse.setPaymentStatus(payment.getPaymentStatus());
        paymentResponse.setPayerFullName(payment.getIsAnonymous() ? "Anonymous" : payment.getPayerFullName());
        paymentResponse.setTransactionCompletedDate(payment.getTransactionCompletedDate());
        paymentResponse.setPaymentProcessor(payment.getPaymentProcessor());
        paymentResponse.setCampaignId(payment.getCampaign().getId());
        paymentResponse.setCampaignTitle(payment.getCampaign().getTitle());
        return paymentResponse;
    }
}
