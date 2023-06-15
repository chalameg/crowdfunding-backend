package com.dxvalley.crowdfunding.exception;

public class PaymentCannotProcessedException extends RuntimeException {
    public PaymentCannotProcessedException(String message) {
        super(message);
    }
}