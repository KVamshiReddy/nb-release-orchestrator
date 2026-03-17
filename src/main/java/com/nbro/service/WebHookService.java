package com.nbro.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbro.domain.common.AppEnums;
import com.nbro.domain.dto.ReleaseRequestDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * Handles all logic related to incoming Jira webhook events.
 * <p>
 * Responsible for two things:
 * 1. Verifying that webhook requests genuinely came from Jira
 * 2. Parsing the Jira payload and creating a release in our system
 * <p>
 * Jira custom field IDs are configurable via application properties
 * so they can be updated without changing the code if they differ
 * across Jira instances.
 */
@Service
@RequiredArgsConstructor
public class WebHookService {

    private static final Logger logger = LoggerFactory.getLogger(WebHookService.class);

    /**
     * Used to create releases from incoming Jira webhook data
     */
    private final ReleaseService releaseService;

    /**
     * The secret key shared between our backend and Jira.
     * Used to compute and verify HMAC-SHA256 signatures.
     */
    @Value("${jira.webhook-secret}")
    private String webhookSecret;

    /**
     * Jira custom field ID for Release Type e.g. Major, Minor, Hotfix
     */
    @Value("${jira.fields.release-type}")
    private String releaseTypeField;

    /**
     * Jira custom field ID for Target Environment e.g. Production, Staging
     */
    @Value("${jira.fields.target-environment}")
    private String targetEnvironmentField;

    /**
     * Jira custom field ID for Scheduled Release Date
     */
    @Value("${jira.fields.scheduled-date}")
    private String scheduledDateField;

    /**
     * Jira custom field ID for Services Impacted
     */
    @Value("${jira.fields.services-impacted}")
    private String servicesImpactedField;

    /**
     * Jira custom field ID for Deployment Steps
     */
    @Value("${jira.fields.deployment-steps}")
    private String deploymentStepsField;

    /**
     * Jira custom field ID for Rollback Plan
     */
    @Value("${jira.fields.rollback-plan}")
    private String rollbackPlanField;

    /**
     * Verifies that a webhook request genuinely came from Jira.
     * <p>
     * How it works:
     * 1. We compute our own HMAC-SHA256 hash of the payload using our secret
     * 2. We compare it to the signature Jira sent in the X-Hub-Signature header
     * 3. If they match, the request is genuine — if not, it's fake or tampered
     * <p>
     * This prevents malicious sources from sending fake releases to our system.
     *
     * @param payload   - the raw JSON body of the webhook request
     * @param signature - the X-Hub-Signature header value from Jira
     * @return true if the signature is valid, false if it is not
     */
    public boolean verifySignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = "sha256=" + HexFormat.of().formatHex(hash);
            return computed.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses an incoming Jira webhook payload and creates a new release.
     * <p>
     * Flow:
     * 1. Parse the raw JSON payload
     * 2. Extract the Jira issue key
     * 3. Check if a release already exists for this key — skip if it does
     * 4. Extract all relevant fields from the payload
     * 5. Map them to a ReleaseRequestDTO
     * 6. Create the release via ReleaseService
     * <p>
     * If anything goes wrong during parsing, the error is logged
     * and the method exits gracefully without crashing the application.
     *
     * @param payload - the raw JSON body sent by Jira
     */
    public void processJiraWebhook(String payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);
            JsonNode fields = root.get("issue").get("fields");

            String jiraKey = root.get("issue").get("key").asText();

            // Skip if release already exists — prevents duplicates
            // Helps when Jira fires multiple events for the same issue
            if (releaseService.releaseExistsByJiraKey(jiraKey)) {
                logger.info("Release already exists for Jira key: {}", jiraKey);
                return;
            }

            // Extract all relevant fields from the Jira payload
            String summary = fields.get("summary").asText();
            String createdBy = root.get("user").get("emailAddress").asText();
            String releaseType = fields.get(releaseTypeField).get("value").asText();
            String targetEnv = fields.get(targetEnvironmentField).get("value").asText();
            String servicesImpacted = fields.get(servicesImpactedField).asText();
            String deploymentSteps = fields.get(deploymentStepsField).asText();
            String rollbackPlan = fields.get(rollbackPlanField).asText();

            ReleaseRequestDTO request = new ReleaseRequestDTO();
            request.setJiraKey(jiraKey);
            request.setTitle(summary);
            request.setCreatedBy(createdBy);
            request.setServicesImpacted(servicesImpacted);
            request.setDeploymentSteps(deploymentSteps);
            request.setRollBackPlan(rollbackPlan);
            request.setReleaseType(AppEnums.ReleaseType.valueOf(releaseType));
            request.setTargetEnvironment(AppEnums.TargetEnvironment.valueOf(targetEnv));

            releaseService.createRelease(request);
            logger.info("Release created from Jira webhook: {}", jiraKey);

        } catch (Exception e) {
            // Log the error but don't throw — we always return 200 to Jira
            // to prevent Jira from retrying the webhook repeatedly
            logger.error("Error processing Jira webhook: {}", e.getMessage());
        }
    }
}