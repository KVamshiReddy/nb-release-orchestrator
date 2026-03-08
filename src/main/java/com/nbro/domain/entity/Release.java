package com.nbro.domain.entity;

import com.nbro.domain.common.AppEnums;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a production release request in the system.
 * Every release starts in Jira and flows through an approval
 * workflow before it reaches production.
 */
@Entity
@Table(name = "releases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Release {

    /** Unique identifier, auto generated */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The Jira issue key e.g. RM-2 */
    @Column(nullable = false, unique = true)
    private String jiraKey;

    /** The release title e.g. v1.0.0 */
    @Column(nullable = false)
    private String title;

    /** Major, Minor, or Hotfix */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppEnums.ReleaseType releaseType;

    /** Production or Staging */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppEnums.TargetEnvironment targetEnvironment;

    /** When the release is scheduled to go out */
    private LocalDateTime scheduledReleaseDate;

    /** Which services will be affected by this release */
    private String servicesImpacted;

    /** Step-by-step instructions for running the deployment */
    private String deploymentSteps;

    /** What to do if the deployment fails */
    private String rollBackPlan;

    /** Checklist items that must be completed before deploying */
    private String preReleaseChecklist;

    /** Notes added after the release is deployed */
    private String postReleaseNotes;

    /** Current status in the workflow — defaults to DRAFT on creation */
    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppEnums.ReleaseStatus releaseStatus = AppEnums.ReleaseStatus.DRAFT;

    /** Email of the developer who created this release */
    private String createdBy;

    /** ID of the DevOps engineer assigned to deploy this release */
    private UUID deployerId;

    /** ID of the release manager responsible for approving this release */
    private UUID releaseManagerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}