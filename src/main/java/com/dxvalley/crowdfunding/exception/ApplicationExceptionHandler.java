package com.dxvalley.crowdfunding.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
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
    @RequiredArgsConstructor
    private class ExceptionResponse {
        private final String timeStamp;
        private final HttpStatus error;
        private final String message;
        private final String requestPath;
    }

    @Autowired
    private DateTimeFormatter dateTimeFormatter;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleInvalidArgument(BindException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Object> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    @ExceptionHandler(PaymentCannotProcessedException.class)
    public ResponseEntity<Object> handlePaymentCannotProcessedException(PaymentCannotProcessedException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.PAYMENT_REQUIRED;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DatabaseAccessException.class)
    public ResponseEntity<?> handleDatabaseAccessException(DatabaseAccessException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                "An unexpected error occurred while processing your request. Please try again later or contact support.",
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                "An unexpected error occurred while processing your request. Please try again later or contact support.",
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }
}


