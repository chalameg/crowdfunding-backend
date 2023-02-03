package com.dxvalley.crowdfunding.exceptions;

public class ApiRequestException extends RuntimeException {
    public ApiRequestException(String message) {
        super(message);
    }

}
