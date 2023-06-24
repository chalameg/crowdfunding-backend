package com.dxvalley.crowdfunding.paymentManager.paymentGateway;

import java.util.Arrays;

public enum PaymentProcessor {
    COOPASS,
    STRIPE,
    CHAPA,
    EBIRR,
    PAYPAL;

    private PaymentProcessor() {
    }

    public static PaymentProcessor lookup(String paymentProcessor) {
        return (PaymentProcessor)Arrays.stream(values()).filter((e) -> {
            return e.name().equalsIgnoreCase(paymentProcessor);
        }).findAny().orElseThrow(() -> {
            return new IllegalArgumentException("Invalid Payment Processor.");
        });
    }
}