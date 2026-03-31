package com.nbro.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for release summary endpoints.
 */
@Getter
@Builder
public class AiSummaryDTO {

    private UUID releaseId;
    private String summary;
    private LocalDateTime generatedAt;
}
