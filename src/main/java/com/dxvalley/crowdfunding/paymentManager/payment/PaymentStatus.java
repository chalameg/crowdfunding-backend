package com.dxvalley.crowdfunding.payment;

import java.util.Arrays;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED;
    public static PaymentStatus lookup(String paymentStatus) {
        return Arrays.stream(PaymentStatus.values())
                .filter(e -> e.name().equalsIgnoreCase(paymentStatus)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Payment Processor."));
    }
}