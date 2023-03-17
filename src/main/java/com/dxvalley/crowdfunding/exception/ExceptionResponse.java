package com.dxvalley.crowdfunding.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class ExceptionResponse {
    private final String timeStamp;
    private final HttpStatus error;
    private final String message;
    }


