package com.dxvalley.crowdfunding.payment;

import java.util.Arrays;
public enum PaymentProcessor {
    COOPASS,
    STRIPE,
    CHAPA,
    EBIRR,
    PAYPAL;

    public static PaymentProcessor lookup(String paymentProcessor) {
        return Arrays.stream(PaymentProcessor.values())
                .filter(e -> e.name().equalsIgnoreCase(paymentProcessor)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Payment Processor."));
    }
}