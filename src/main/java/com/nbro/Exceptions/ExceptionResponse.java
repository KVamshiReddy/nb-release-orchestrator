package com.nbro.Exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a structured error response returned to the client
 * when something goes wrong in the application.
 * Example response:
 * {
 * "timeStamp": "2026-03-08T20:42:13",
 * "message": "A release with this Jira key already exists: RM-1",
 * "errorCode": 400,
 * "details": "Bad Request"
 * }
 */
@Data
@Builder
public class ExceptionResponse {

    /**
     * When the error occurred
     */
    private LocalDateTime timeStamp;

    /**
     * A human-readable description of what went wrong
     */
    private String message;

    /**
     * The HTTP status code e.g. 400, 500
     */
    private int errorCode;

    /**
     * A short label for the error type e.g. "Bad Request", "Internal Server Error"
     */
    private String details;

    /**
     * Use this constructor when you want to set a specific timestamp.
     * Useful for testing or when you need precise control over the timestamp.
     */
    public ExceptionResponse(LocalDateTime timeStamp, String message, int errorCode, String details) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.errorCode = errorCode;
        this.details = details;
    }

    /**
     * Use this constructor when you don't care about the exact timestamp.
     * Automatically sets the timestamp to the current date and time.
     * This is the most commonly used constructor.
     */
    public ExceptionResponse(String message, int errorCode, String details) {
        this.timeStamp = LocalDateTime.now();
        this.message = message;
        this.errorCode = errorCode;
        this.details = details;
    }
}