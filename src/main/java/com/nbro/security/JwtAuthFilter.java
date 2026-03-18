package com.nbro.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * This class acts like a security checkpoint for every incoming request.
 * It runs exactly once per request, checks if the token is valid,
 * and if so, tells Spring Security who the user is.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * This is our JWT utility class that handles token validation
     * and extracting information from tokens.
     */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * This method runs automatically on every single incoming HTTP request.
     * It checks if the request has a valid JWT token and if so,
     * marks the user as authenticated for this request.
     *
     * @param request     - the incoming HTTP request (contains headers, body etc.)
     * @param response    - the outgoing HTTP response
     * @param filterChain - the chain of filters — we must call this at the end
     *                    to pass the request to the next filter or controller
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        // Every request that requires authentication must include an
        // "Authorization" header that looks like: "Bearer eyJhbGc..."
        // We read that header here.
        String header = request.getHeader("Authorization");

        // Check if the header exists and starts with "Bearer "
        // If it doesn't, we skip token validation entirely and just
        // pass the request along — Spring Security will block it later
        // if the endpoint requires authentication.
        if (header != null && header.startsWith("Bearer ")) {

            // Remove the "Bearer " prefix (7 characters) to get just the token
            // Example: "Bearer abc123" becomes "abc123"
            String token = header.substring(7);

            // Ask our JWT utility to check if the token is valid
            // (not expired, not tampered with, correctly signed)
            boolean isValid = jwtTokenProvider.validateToken(token);

            if (isValid) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);

                // Convert role string to a GrantedAuthority Spring Security understands
                List<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(role)
                );

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Always pass the request to the next filter in the chain
        // This MUST be called regardless of whether the token was valid or not
        // Without this line, the request would never reach the controller
        filterChain.doFilter(request, response);
    }
}