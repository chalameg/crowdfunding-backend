package com.dxvalley.crowdfunding.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorMap;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

}


