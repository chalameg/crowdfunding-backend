package com.dxvalley.crowdfunding.exceptions;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        // TODO Auto-generated method stub

        // Map<String, Object> responseBody = new LinkedHashMap<>();
        // responseBody.put("timestamp", new Date());
        // responseBody.put("status", status.value());
        // List<String> errors = ex.getBindingResult().getFieldErrors()
        //     .stream()
        //     .map(x -> x.getDefaultMessage())
        //     .collect(Collectors.toList());
                
        // responseBody.put("errors", errors);

        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

}
