package com.nbro.config;

import com.nbro.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class controls who can access what in our application.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * This is our custom JWT checker. It reads the token from
     * every incoming request and decides if the user is logged in.
     */
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * All the security rules for our application.
     *
     * @param http - Spring gives us this object to configure security settings
     * @return the final security setup
     * @throws Exception if something goes wrong during setup
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF(Cross Site Request Forgery) is a type of attack where a malicious website tricks a user
                // into making unwanted requests. We disable it here because we use
                // JWT tokens instead of browser cookies, so this attack doesn't apply to us.
                .csrf(csrf -> csrf.disable())

                // We tell Spring NOT to remember who is logged in between requests.
                // Instead, the user must send their JWT token with EVERY request.
                // This is called "stateless" — the server never stores login sessions.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Here we define which pages/endpoints are open to everyone and which ones require the user to be logged in.
                .authorizeHttpRequests(auth -> auth
                        // These endpoints don't need a token:
                        // /api/v1/auth/** — login page (you can't need a token to log in!)
                        // /swagger-ui/** — API documentation page
                        // /v3/api-docs/** — Swagger technical docs
                        .requestMatchers("/api/v1/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Every other endpoint requires a valid JWT token
                        .anyRequest().authenticated())

                // We add our JWT checker BEFORE Spring's default login checker.
                // This means our token validation runs first on every request.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}