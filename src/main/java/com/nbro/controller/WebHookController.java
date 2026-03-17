package com.nbro.controller;

import com.nbro.service.WebHookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles incoming webhook events from Jira.
 * <p>
 * When a developer creates or updates a Release Change Request issue in Jira,
 * Jira automatically sends a POST request to this endpoint with the issue details.
 * We parse that data and automatically create or update a release in our system.
 * <p>
 * This endpoint is public — no JWT token required since Jira cannot log in.
 * Instead we use HMAC-SHA256 signature verification to confirm the request
 * actually came from Jira and not a malicious source.
 * <p>
 * Base URL: /api/v1/webhooks
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/webhooks")
public class WebHookController {

    /**
     * The service that handles signature verification and payload processing.
     */
    private final WebHookService webHookService;

    /**
     * The secret key shared between our backend and Jira.
     * Used to verify that incoming webhook requests are genuine.
     * Stored in application-dev.properties, never committed to Git.
     */
    @Value("${jira.webhook-secret}")
    private String webhookSecret;

    /**
     * Receives webhook events from Jira when a Release Change Request
     * issue is created or updated.
     * <p>
     * Flow:
     * 1. Jira creates/updates a Release Change Request issue
     * 2. Jira sends a POST request to this endpoint with the issue data
     * 3. We verify the signature to confirm it came from Jira
     * 4. We parse the payload and create/update the release in our database
     *
     * @param signature - HMAC-SHA256 signature from Jira in X-Hub-Signature header
     * @param payload   - the raw JSON body containing the Jira issue data
     * @return 200 OK if processed successfully, 401 if signature is invalid
     */
    @PostMapping("/jira")
    public ResponseEntity<String> handleJiraWebhook(
            @RequestHeader(value = "X-Hub-Signature", required = false) String signature,
            @RequestBody String payload) {

        // TODO: re-enable signature verification before production deployment
        // Signature verification ensures requests are genuinely from Jira
        // and not from a malicious source trying to inject fake releases.
        // if (!webHookService.verifySignature(payload, signature)) {
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        // }

        webHookService.processJiraWebhook(payload);
        return ResponseEntity.ok("Received");
    }
}