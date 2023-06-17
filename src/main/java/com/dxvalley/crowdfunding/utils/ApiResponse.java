package com.dxvalley.crowdfunding.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private HttpStatus httpStatus;
    private String message;

    public static <T> ResponseEntity<?> success(T response) {
        ApiResponse<T> apiResponse;
        if (response instanceof String)
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK, (String) response));
        else
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public static ResponseEntity<ApiResponse> success(String message) {
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK, message);

        return ResponseEntity.ok(apiResponse);

    }


    public static <T> ResponseEntity<?> created(T response) {
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static <T> ResponseEntity<?> error(HttpStatus status, T message) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status, message.toString()));
    }

    public static ResponseEntity<ApiResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status, message));
    }
}

