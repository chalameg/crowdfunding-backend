package com.dxvalley.crowdfunding.exception.handler;

import com.dxvalley.crowdfunding.exception.customException.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApplicationExceptionHandler {
    private final DateTimeFormatter dateTimeFormatter;
    public static final String DB_ERROR_MESSAGE = "Sorry, we encountered an error.Please try again later or contact support.";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class})
    public ResponseEntity<Object> handleInvalidArgument(BindException ex) {
        Map<String, String> errorMap = new HashMap();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ExceptionResponse apiException = new ExceptionResponse(LocalDateTime.now().format(this.dateTimeFormatter), httpStatus, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({ResourceAlreadyExistsException.class})
    public ResponseEntity<Object> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        ExceptionResponse apiException = new ExceptionResponse(LocalDateTime.now().format(this.dateTimeFormatter), httpStatus, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ExceptionResponse apiException = new ExceptionResponse(LocalDateTime.now().format(this.dateTimeFormatter), httpStatus, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MultipartException.class})
    public ResponseEntity<Object> handleMultipartException(MultipartException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ExceptionResponse apiException = new ExceptionResponse(LocalDateTime.now().format(this.dateTimeFormatter), httpStatus, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        ExceptionResponse apiException = new ExceptionResponse(LocalDateTime.now().format(this.dateTimeFormatter), httpStatus, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }

    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    @ExceptionHandler({PaymentCannotProcessedException.class})
    public ResponseEntity<Object> handlePaymentCannotProcessedException(PaymentCannotProcessedException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.PAYMENT_REQUIRED;
        ExceptionResponse apiException = new ExceptionResponse(LocalDateTime.now().format(this.dateTimeFormatter), httpStatus, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({DatabaseAccessException.class})
    public ResponseEntity<ExceptionResponse> handleDatabaseAccessException(DatabaseAccessException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponse apiException = new ExceptionResponse(LocalDateTime.now().format(this.dateTimeFormatter), httpStatus, "Sorry, we encountered an error.Please try again later or contact support.", request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionResponse> handleException(Exception ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponse apiException = new ExceptionResponse(LocalDateTime.now().format(this.dateTimeFormatter), httpStatus, "An unexpected error occurred while processing your request. Please try again later or contact support.", request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }
}


