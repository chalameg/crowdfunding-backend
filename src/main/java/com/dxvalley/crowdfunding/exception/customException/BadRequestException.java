package com.dxvalley.crowdfunding.exception.customException;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

