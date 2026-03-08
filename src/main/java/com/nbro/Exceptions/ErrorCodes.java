package com.nbro.Exceptions;

/**
 * Centralized list of custom error codes for the application.
 * <p>
 * Using numeric error codes instead of just HTTP status codes gives
 * clients a more specific reason for the failure. For example,
 * both "user not found" and "invalid credentials" return HTTP 400,
 * but the error code tells the client exactly which one it was.
 * <p>
 * Error code ranges:
 * 1001 - 1029 → General errors (missing params, bad data, not found)
 * 1030 - 1050 → User related errors (duplicate user, wrong password)
 */
public interface ErrorCodes {

    // ─── General Errors (1001 - 1030) ────────────────────────────────

    /**
     * A required parameter was not provided in the request
     */
    int MISSING_PARAM = 1001;

    /**
     * The request is malformed or contains invalid data
     */
    int BAD_REQUEST = 1002;

    /**
     * One or more fields failed validation
     */
    int DETAILS_NOT_VALID = 1003;

    /**
     * The requested record could not be found in the database
     */
    int RECORD_NOT_FOUND = 1004;

    /**
     * The user does not have permission to perform this action
     */
    int NOT_AUTHORIZED = 1005;

    // ─── User Related Errors (1030 - 1050) ───────────────────────────

    /**
     * A user with this email already exists in the system
     */
    int USER_ALREADY_EXIST = 1030;

    /**
     * The email or password provided is incorrect
     */
    int INVALID_CREDENTIALS = 1031;

    /**
     * No user was found with the provided identifier
     */
    int USER_NOT_FOUND = 1033;
}