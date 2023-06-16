package com.dxvalley.crowdfunding.exception.customException;

public class PaymentCannotProcessedException extends RuntimeException {
    public PaymentCannotProcessedException(String message) {
        super(message);
    }
}