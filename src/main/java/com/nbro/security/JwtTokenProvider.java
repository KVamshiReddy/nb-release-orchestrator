package com.nbro.security;

import com.nbro.domain.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * This class is responsible for everything related to JWT tokens.
 * Think of it as a token factory — it creates tokens when users log in,
 * reads information from tokens on incoming requests, and checks
 * whether a token is still valid or has been tampered with.
 * <p>
 * A JWT token looks like this: eyJhbGc.eyJzdWIi.SflKxw
 * It has three parts separated by dots:
 * 1. Header — what algorithm was used to sign it
 * 2. Payload — the actual data (email, role, expiry)
 * 3. Signature — proves the token hasn't been tampered with
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    /**
     * The secret key used to sign and verify tokens.
     * Think of this as the stamp that only our server knows.
     * If someone tries to fake a token without knowing this secret,
     * the signature check will fail, and we will reject it.
     */
    @Value("${security.jwt.secret}")
    private String secret;

    /**
     * How long a token stays valid, in milliseconds.
     * After this time, the token expires and the user must log in again.
     * Default is 86400000ms which equals 24 hours
     */
    @Value("${security.jwt.expiry-ms}")
    private long lifeSpan;

    /**
     * Converts our plain text secret into a cryptographic key
     * that the JJWT library can use to sign and verify tokens.
     *
     * @return a SecretKey object ready to use for signing
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Creates a new JWT token for a user after they successfully log in.
     * The token contains the user's email and role so we can identify
     * them on future requests without hitting the database every time.
     * <p>
     * Example token content (decoded):
     * {
     * "sub": "vamshi@example.com",
     * "role": "ROLE_DEV",
     * "iat": 1234567890,   <- when it was created
     * "exp": 1234654290    <- when it expires
     * }
     *
     * @param user - the user who just logged in
     * @return a signed JWT token string to send back to the user
     */
    public String generateToken(User user) {
        return Jwts.builder()
                // Store the user's email as the main identifier
                .subject(user.getEmail())
                // Store the user's role so we know their permissions
                .claim("role", user.getRole())
                // Record when this token was created
                .issuedAt(new Date())
                // Set when this token will stop being valid
                .expiration(new Date(System.currentTimeMillis() + lifeSpan))
                // Sign the token with our secret key
                // This prevents anyone from creating fake tokens
                .signWith(getSecretKey())
                // Build the final token string
                .compact();
    }

    /**
     * Reads a JWT token and extracts the user's email from it.
     * This is used on every authenticated request to know who is making it.
     * <p>
     * If the token is expired or tampered with, this method will
     * throw an exception which is caught by validateToken.
     *
     * @param token - the JWT token string from the Authorization header
     * @return the email address stored inside the token
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                // Use our secret key to verify the token's signature
                .verifyWith(getSecretKey())
                .build()
                // Parse and verify the token
                .parseSignedClaims(token)
                // Get the payload (the data stored inside)
                .getPayload()
                // Extract the email (stored as the "subject")
                .getSubject();
    }

    /**
     * Checks whether a JWT token is valid.
     * A token is valid if it:
     * - Has not expired
     * - Has not been tampered with
     * - Was signed with our secret key
     * <p>
     * We do this by simply trying to read the email from the token.
     * If that succeeds, the token is valid.
     * If anything goes wrong, the token is invalid and we return false.
     *
     * @param token - the JWT token string to validate
     * @return true if the token is valid, false if it is not
     */
    public boolean validateToken(String token) {
        try {
            getEmailFromToken(token);
            return true;
        } catch (Exception e) {
            // Log the reason the token failed validation
            // Common reasons: expired, wrong signature, malformed
            logger.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public String getRoleFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
}