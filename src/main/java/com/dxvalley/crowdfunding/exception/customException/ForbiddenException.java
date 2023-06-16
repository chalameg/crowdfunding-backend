package com.dxvalley.crowdfunding.exception.customException;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
