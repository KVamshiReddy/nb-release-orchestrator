package com.nbro.domain.dto;

import com.nbro.domain.common.AppEnums;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for risk assessment endpoints.
 */
@Getter
@Builder
public class AiRiskAssessmentDTO {

    private UUID id;
    private UUID releaseId;
    private AppEnums.RiskLevel riskLevel;
    private String reasoning;
    private String suggestions;
    private LocalDateTime generatedAt;
    private String modelUsed;
}
