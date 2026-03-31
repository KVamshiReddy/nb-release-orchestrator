package com.nbro.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Response DTO for GET /api/v1/dashboard/stats
 * Returns counts of releases grouped by status.
 */
@Getter
@Builder
public class DashboardStatsDTO {

    /** Total number of releases in the system */
    private long totalReleases;

    /** Counts per release status e.g. {"DRAFT": 5, "APPROVED": 3} */
    private Map<String, Long> countsByStatus;
}
