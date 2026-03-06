package com.nbro.controller;

import com.nbro.domain.dto.LoginRequest;
import com.nbro.domain.dto.LoginResponse;
import com.nbro.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles all incoming HTTP requests related to authentication.
 *
 * This is the entry point for any user trying to log in.
 * It receives the request, passes it to AuthService for processing,
 * and returns the result back to the caller.
 *
 * All endpoints in this controller are public — no JWT token required.
 * This is configured in SecurityConfig under the permitAll() rules.
 *
 * Base URL: /api/v1/auth
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    /**
     * The service that handles the actual login logic.
     * The controller just receives and returns data —
     * all business logic lives in AuthService.
     */
    private final AuthService authService;

    /**
     * Authenticates a user and returns a JWT token.
     *
     * How to use:
     * POST /api/v1/auth/login
     * Body: { "email": "user@example.com", "password": "yourpassword" }
     *
     * Success response:
     * { "token": "eyJhbGc...", "email": "user@example.com", "role": "ROLE_DEV" }
     *
     * The token returned here must be included in all future requests
     * as an Authorization header: "Bearer eyJhbGc..."
     *
     * @param request - contains the email and password from the request body
     * @return LoginResponse - contains the JWT token, email, and role
     */
    @Operation(summary = "User logs in with email and password and receives a JWT token")
    @PostMapping("login")
    public LoginResponse loginUser(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}