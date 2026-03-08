package com.nbro.Exceptions;

/**
 * Central place for all error messages used across the application.
 * <p>
 * Instead of hardcoding error strings in every service class,
 * we define them all here. This means if we ever want to change
 * a message we only change it in one place.
 * <p>
 * Messages with %s are templates — use String.format() to fill them in.
 * Example: String.format(ErrorMessages.RECORD_NOT_FOUND, "Release")
 * Result:  "Could Not Find Release"
 */
public interface ErrorMessages {

    // --- User Errors ---

    /**
     * Thrown when trying to create a user that already exists
     */
    String USER_ALREADY_EXIST = "User Already exists";

    /**
     * Thrown when a user tries to perform an action their role does not allow
     */
    String NOT_AUTHORIZED = "User is not authorized for this action";

    /**
     * Thrown when a record cannot be found. Use %s to specify what was not found
     */
    String RECORD_NOT_FOUND = "Could Not Find %s";

    /**
     * Thrown when login credentials are wrong. Use %s to include additional context
     */
    String INVALID_CREDENTIALS = "Invalid credentials %s";

    // --- General Errors ---

    /**
     * Thrown when the request body contains invalid or missing fields
     */
    String DETAILS_NOT_VALID = "Details provided are not valid";

    // --- Release Errors ---

    /**
     * Thrown when creating a release with a Jira key that already exists in the system
     */
    String DUPLICATE_JIRA_KEY = "A release with this Jira key already exists: %s";

    /**
     * Thrown when the Jira key does not follow the required format e.g. RM-1, SCRUM-5
     */
    String INVALID_JIRA_KEY_FORMAT = "Invalid key format - Expected format: PROJECT-123";

    /**
     * Thrown when a release cannot be found by its ID
     */
    String RELEASE_NOT_FOUND = "Could Not Find Release with id: %s";
}