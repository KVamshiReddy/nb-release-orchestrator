package com.nbro.domain.dto;

import com.nbro.domain.common.AppEnums;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the data a user submits when creating a new release request.
 * <p>
 * A DTO (Data Transfer Object) is a simple container for data moving
 * between the client and the server. It only contains the fields the
 * user is allowed to set — things like id, status, and timestamps are
 * excluded because they are set automatically by the system.
 */
@Data
public class ReleaseRequestDTO {

    /**
     * The Jira issue key e.g. RM-1, SCRUM-5. Must follow PROJECT-123 format.
     */
    private String jiraKey;

    /**
     * The release title e.g. "v1.0.0 - Payment Service Update"
     */
    private String title;

    /**
     * Whether this is a MAJOR, MINOR, or HOTFIX release
     */
    private AppEnums.ReleaseType releaseType;

    /**
     * Where this release will be deployed — PRODUCTION or STAGING
     */
    private AppEnums.TargetEnvironment targetEnvironment;

    /**
     * When the release is planned to go out
     */
    private LocalDateTime scheduledReleaseDate;

    /**
     * Which services will be affected by this release
     */
    private String servicesImpacted;

    /**
     * Step-by-step instructions for running the deployment
     */
    private String deploymentSteps;

    /**
     * What to do if the deployment fails and needs to be reversed
     */
    private String rollBackPlan;

    /**
     * Checklist items that must be completed before deploying
     */
    private String preReleaseChecklist;

    /**
     * Notes added after the release is deployed
     */
    private String postReleaseNotes;

    /**
     * Email of the developer creating this release request
     */
    private String createdBy;

    /**
     * ID of the DevOps engineer who will run the deployment
     */
    private UUID deployerId;

    /**
     * ID of the release manager who will approve this release
     */
    private UUID releaseManagerId;
}