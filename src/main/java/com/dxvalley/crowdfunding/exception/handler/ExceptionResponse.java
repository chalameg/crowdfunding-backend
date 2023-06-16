package com.dxvalley.crowdfunding.exception.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ExceptionResponse {
    private final String timeStamp;
    private final HttpStatus error;
    private final String message;
    private final String requestPath;
}
