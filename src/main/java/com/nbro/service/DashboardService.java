package com.nbro.service;

import com.nbro.domain.common.AppEnums;
import com.nbro.domain.dto.CalendarReleaseDTO;
import com.nbro.domain.dto.DashboardStatsDTO;
import com.nbro.domain.entity.Release;
import com.nbro.repository.ReleaseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    private final ReleaseRepository releaseRepository;

    /**
     * Returns total release count and a breakdown by status.
     */
    public DashboardStatsDTO getStats() {
        List<Object[]> rows = releaseRepository.countGroupedByStatus();

        Map<String, Long> countsByStatus = new LinkedHashMap<>();
        long total = 0L;

        // Pre-populate all statuses with 0 so the frontend always gets every key
        for (AppEnums.ReleaseStatus status : AppEnums.ReleaseStatus.values()) {
            countsByStatus.put(status.name(), 0L);
        }

        for (Object[] row : rows) {
            AppEnums.ReleaseStatus status = (AppEnums.ReleaseStatus) row[0];
            long count = (Long) row[1];
            countsByStatus.put(status.name(), count);
            total += count;
        }

        logger.info("Dashboard stats requested — total releases: {}", total);

        return DashboardStatsDTO.builder()
                .totalReleases(total)
                .countsByStatus(countsByStatus)
                .build();
    }

    /**
     * Returns releases that have a scheduled date, ordered chronologically.
     * Useful for rendering a release calendar on the dashboard.
     */
    public List<CalendarReleaseDTO> getCalendar() {
        List<Release> releases = releaseRepository
                .findAllByScheduledReleaseDateIsNotNullOrderByScheduledReleaseDateAsc();

        logger.info("Calendar requested — {} scheduled releases found", releases.size());

        return releases.stream()
                .map(r -> CalendarReleaseDTO.builder()
                        .id(r.getId())
                        .jiraKey(r.getJiraKey())
                        .title(r.getTitle())
                        .releaseStatus(r.getReleaseStatus())
                        .releaseType(r.getReleaseType())
                        .targetEnvironment(r.getTargetEnvironment())
                        .scheduledReleaseDate(r.getScheduledReleaseDate())
                        .build())
                .collect(Collectors.toList());
    }
}
