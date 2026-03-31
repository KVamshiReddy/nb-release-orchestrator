package com.nbro.domain.dto;

import com.nbro.domain.common.AppEnums;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A compact release representation for the calendar view.
 * Groups releases by scheduled date.
 */
@Getter
@Builder
public class CalendarReleaseDTO {

    private UUID id;
    private String jiraKey;
    private String title;
    private AppEnums.ReleaseStatus releaseStatus;
    private AppEnums.ReleaseType releaseType;
    private AppEnums.TargetEnvironment targetEnvironment;
    private LocalDateTime scheduledReleaseDate;
}
