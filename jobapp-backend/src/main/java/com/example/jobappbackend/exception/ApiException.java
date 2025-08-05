package com.example.jobappbackend.exception;

/**
 * Custom exception for API-level errors.
 */
public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
