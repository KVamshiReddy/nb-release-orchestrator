package com.nbro.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbro.Exceptions.ErrorMessages;
import com.nbro.Exceptions.ResourceNotFoundException;
import com.nbro.domain.common.AppEnums;
import com.nbro.domain.dto.AiChatResponseDTO;
import com.nbro.domain.dto.AiRiskAssessmentDTO;
import com.nbro.domain.dto.AiSummaryDTO;
import com.nbro.domain.entity.AiRiskAssessment;
import com.nbro.domain.entity.Release;
import com.nbro.repository.AiRiskAssessmentRepository;
import com.nbro.repository.ReleaseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReleaseAiService {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseAiService.class);

    private final ReleaseRepository releaseRepository;
    private final AiRiskAssessmentRepository aiRiskAssessmentRepository;
    private final AIService aiService;
    private final ObjectMapper objectMapper;

    @Value("${groq.model}")
    private String modelUsed;

    // ─────────────────────────────────────────────────────────────
    // Risk Scoring
    // ─────────────────────────────────────────────────────────────

    /**
     * Generates a new risk assessment for the given release and saves it.
     */
    @Transactional
    public AiRiskAssessmentDTO generateRisk(UUID releaseId) {
        Release release = getRelease(releaseId);

        String prompt = buildRiskPrompt(release);
        logger.info("Sending risk scoring prompt to AI for release {}", releaseId);

        String rawResponse = aiService.generateResponse(prompt);
        AiRiskAssessment assessment = parseRiskResponse(rawResponse, releaseId);

        AiRiskAssessment saved = aiRiskAssessmentRepository.save(assessment);
        logger.info("Risk assessment saved for release {}: {}", releaseId, saved.getRiskLevel());

        return toRiskDTO(saved);
    }

    /**
     * Returns the most recent risk assessment for the given release.
     */
    public AiRiskAssessmentDTO getLatestRisk(UUID releaseId) {
        getRelease(releaseId); // ensure release exists
        AiRiskAssessment assessment = aiRiskAssessmentRepository
                .findTopByReleaseIdOrderByGeneratedAtDesc(releaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.RECORD_NOT_FOUND, "Risk assessment for release " + releaseId)));
        return toRiskDTO(assessment);
    }

    // ─────────────────────────────────────────────────────────────
    // Summary Generation
    // ─────────────────────────────────────────────────────────────

    /**
     * Generates a human-readable summary and stores it on the release.
     */
    @Transactional
    public AiSummaryDTO generateSummary(UUID releaseId) {
        Release release = getRelease(releaseId);

        String prompt = buildSummaryPrompt(release);
        logger.info("Sending summary prompt to AI for release {}", releaseId);

        String summary = aiService.generateResponse(prompt);
        release.setAiGeneratedSummary(summary.trim());
        release.setAiSummaryGeneratedAt(LocalDateTime.now());
        releaseRepository.save(release);

        logger.info("AI summary saved for release {}", releaseId);

        return AiSummaryDTO.builder()
                .releaseId(releaseId)
                .summary(summary.trim())
                .generatedAt(release.getAiSummaryGeneratedAt())
                .build();
    }

    /**
     * Returns the stored AI summary for the given release.
     */
    public AiSummaryDTO getSummary(UUID releaseId) {
        Release release = getRelease(releaseId);
        if (release.getAiGeneratedSummary() == null) {
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.RECORD_NOT_FOUND, "AI summary for release " + releaseId));
        }
        return AiSummaryDTO.builder()
                .releaseId(releaseId)
                .summary(release.getAiGeneratedSummary())
                .generatedAt(release.getAiSummaryGeneratedAt())
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // Chat Assistant
    // ─────────────────────────────────────────────────────────────

    /**
     * RAG-lite assistant: injects release context into the prompt
     * so the AI can answer questions about the current state of releases.
     */
    public AiChatResponseDTO chat(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.DETAILS_NOT_VALID);
        }

        String context = buildReleaseContext();
        String prompt = buildChatPrompt(context, userMessage.trim());

        logger.info("Sending chat prompt to AI");
        String answer = aiService.generateResponse(prompt);

        return AiChatResponseDTO.builder()
                .question(userMessage.trim())
                .answer(answer.trim())
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────

    private Release getRelease(UUID releaseId) {
        return releaseRepository.findById(releaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.RELEASE_NOT_FOUND, releaseId)));
    }

    private String buildRiskPrompt(Release release) {
        return """
                You are a release risk assessment expert. Analyze the following release and respond ONLY with a valid JSON object.

                Release details:
                - Jira Key: %s
                - Title: %s
                - Type: %s
                - Target Environment: %s
                - Scheduled Date: %s
                - Services Impacted: %s
                - Deployment Steps: %s
                - Rollback Plan: %s
                - Pre-Release Checklist: %s

                Respond with this exact JSON format (no markdown, no explanation outside the JSON):
                {
                  "riskLevel": "LOW" | "MEDIUM" | "HIGH",
                  "reasoning": "2-3 sentences explaining the risk level",
                  "suggestions": "2-3 concrete steps to reduce risk"
                }
                """.formatted(
                release.getJiraKey(),
                release.getTitle(),
                release.getReleaseType(),
                release.getTargetEnvironment(),
                release.getScheduledReleaseDate(),
                release.getServicesImpacted(),
                release.getDeploymentSteps(),
                release.getRollBackPlan(),
                release.getPreReleaseChecklist()
        );
    }

    private String buildSummaryPrompt(Release release) {
        return """
                You are a release management assistant. Write a concise, professional, human-readable summary of the following release in 3-5 sentences. Focus on what is being released, why it matters, what environments are affected, and the key risks or considerations.

                Release details:
                - Jira Key: %s
                - Title: %s
                - Type: %s
                - Target Environment: %s
                - Scheduled Date: %s
                - Status: %s
                - Services Impacted: %s
                - Deployment Steps: %s
                - Rollback Plan: %s
                - Post-Release Notes: %s

                Write only the summary paragraph, no headers or bullet points.
                """.formatted(
                release.getJiraKey(),
                release.getTitle(),
                release.getReleaseType(),
                release.getTargetEnvironment(),
                release.getScheduledReleaseDate(),
                release.getReleaseStatus(),
                release.getServicesImpacted(),
                release.getDeploymentSteps(),
                release.getRollBackPlan(),
                release.getPostReleaseNotes()
        );
    }

    private String buildReleaseContext() {
        List<Release> releases = releaseRepository.findAll();
        if (releases.isEmpty()) {
            return "There are currently no releases in the system.";
        }

        String releaseSummaries = releases.stream()
                .map(r -> "- %s | %s | Status: %s | Env: %s | Scheduled: %s".formatted(
                        r.getJiraKey(),
                        r.getTitle(),
                        r.getReleaseStatus(),
                        r.getTargetEnvironment(),
                        r.getScheduledReleaseDate() != null ? r.getScheduledReleaseDate().toLocalDate() : "TBD"))
                .collect(Collectors.joining("\n"));

        return "Current releases in the system:\n" + releaseSummaries;
    }

    private String buildChatPrompt(String context, String userMessage) {
        return """
                You are a helpful release management assistant for NB Release Orchestrator. Answer questions about releases using the context provided below. Be concise and factual.

                %s

                User question: %s
                """.formatted(context, userMessage);
    }

    private AiRiskAssessment parseRiskResponse(String rawResponse, UUID releaseId) {
        try {
            // Strip markdown code fences if present
            String cleaned = rawResponse.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("```[a-z]*\\n?", "").replaceAll("```", "").trim();
            }

            JsonNode json = objectMapper.readTree(cleaned);
            String riskLevelStr = json.get("riskLevel").asText().toUpperCase();
            AppEnums.RiskLevel riskLevel;
            try {
                riskLevel = AppEnums.RiskLevel.valueOf(riskLevelStr);
            } catch (IllegalArgumentException e) {
                logger.warn("AI returned unknown risk level '{}', defaulting to MEDIUM", riskLevelStr);
                riskLevel = AppEnums.RiskLevel.MEDIUM;
            }

            return AiRiskAssessment.builder()
                    .releaseId(releaseId)
                    .riskLevel(riskLevel)
                    .reasoning(json.path("reasoning").asText("No reasoning provided"))
                    .suggestions(json.path("suggestions").asText("No suggestions provided"))
                    .modelUsed(modelUsed)
                    .build();

        } catch (Exception e) {
            logger.error("Failed to parse AI risk response, defaulting to MEDIUM: {}", e.getMessage());
            return AiRiskAssessment.builder()
                    .releaseId(releaseId)
                    .riskLevel(AppEnums.RiskLevel.MEDIUM)
                    .reasoning("Could not parse AI response. Manual review recommended.")
                    .suggestions(rawResponse.length() > 500 ? rawResponse.substring(0, 500) : rawResponse)
                    .modelUsed(modelUsed)
                    .build();
        }
    }

    private AiRiskAssessmentDTO toRiskDTO(AiRiskAssessment a) {
        return AiRiskAssessmentDTO.builder()
                .id(a.getId())
                .releaseId(a.getReleaseId())
                .riskLevel(a.getRiskLevel())
                .reasoning(a.getReasoning())
                .suggestions(a.getSuggestions())
                .generatedAt(a.getGeneratedAt())
                .modelUsed(a.getModelUsed())
                .build();
    }
}
