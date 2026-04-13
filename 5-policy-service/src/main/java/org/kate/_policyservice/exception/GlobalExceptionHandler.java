package org.kate.exception;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import org.kate._policyservice.model.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        // Wrap the errors map inside Argument 3 (Data)
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                false,             // Argument 1: Success
                "Validation failed", // Argument 2: Message
                errors             // Argument 3: Data (The map of specific field errors)
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(
                new ApiResponse(false, ex.getMessage(), null),
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(
                new ApiResponse(false, "An unexpected error occurred", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedException(BadCredentialsException ex) {
        return new ResponseEntity<>(
                new ApiResponse(false, "Invalid username or password", null),
                HttpStatus.UNAUTHORIZED
        );
    }
    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ApiResponse<Object>> handleServiceUnavailable(DataAccessResourceFailureException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                false,
                "Service is temporarily unavailable. Please try again later.",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }}

