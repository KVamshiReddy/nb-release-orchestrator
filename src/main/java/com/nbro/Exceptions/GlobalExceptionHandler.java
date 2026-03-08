package com.nbro.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Catches exceptions thrown anywhere in the application and returns
 * a clean, readable JSON error response instead of a generic error page.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors — things the user did wrong.
     * Example: invalid Jira key format, duplicate key, empty title.
     * Returns HTTP 400 Bad Request.
     *
     * @param e - the exception containing the error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgument(IllegalArgumentException e) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                e.getMessage(),
                400,
                "Bad Request"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles unexpected server errors — things that went wrong internally.
     * Example: database is down, record not found, unexpected null value.
     * Returns HTTP 500 Internal Server Error.
     *
     * @param e - the exception containing the error message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException e) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                e.getMessage(),
                500,
                "Internal Server Error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}