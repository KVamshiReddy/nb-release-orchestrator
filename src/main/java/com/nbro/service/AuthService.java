package com.nbro.service;

import com.nbro.domain.dto.LoginRequest;
import com.nbro.domain.dto.LoginResponse;
import com.nbro.domain.entity.User;
import com.nbro.repository.UserRepository;
import com.nbro.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles all authentication related business logic.
 * Currently responsible for logging users in and generating JWT tokens.
 *
 * This is the middle layer between the controller (which receives the request)
 * and the database/security utilities (which do the actual work).
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /**
     * Used to look up users from the database by their email address.
     */
    private final UserRepository userRepository;

    /**
     * Used to generate a JWT token after the user is verified.
     */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Used to verify the user's password.
     * We never store plain text passwords — only hashed versions.
     * This encoder hashes the entered password and compares it
     * to the stored hash without ever decrypting it.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user and returns a JWT token if successful.
     *
     * Step by step:
     * 1. Look up the user by email in the database
     * 2. If not found, throw an error
     * 3. Check if the entered password matches the stored hash
     * 4. If it doesn't match, throw an error
     * 5. Generate a JWT token for the user
     * 6. Return the token along with the user's email and role
     *
     * @param request - contains the email and password entered by the user
     * @return LoginResponse - contains the JWT token, email, and role
     * @throws RuntimeException if the user is not found or password is wrong
     */
    public LoginResponse login(LoginRequest request) {

        // Look up the user by email — case-insensitive so
        // "Admin@nbro.com" and "admin@nbro.com" both work
        // If no user is found, throw an error immediately
        User user = userRepository
                .findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        // Hash the entered password and compare it to the stored hash
        // If they don't match, the password is wrong
        // We intentionally use a vague error message so attackers
        // can't tell whether the email or password was wrong
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Username or the Password are invalid");
        }

        // Password is correct — generate a JWT token for this user
        // The token contains the user's email and role
        String token = jwtTokenProvider.generateToken(user);

        // Build the response object with the token and user details
        LoginResponse response = new LoginResponse();
        response.setEmail(user.getEmail());
        response.setRole(String.valueOf(user.getRole()));
        response.setToken(token);

        // Log successful login for monitoring purposes
        // We only log the email, never the password
        logger.info("User logged in successfully: {}", user.getEmail());

        return response;
    }
}