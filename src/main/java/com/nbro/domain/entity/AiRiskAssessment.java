package com.nbro.domain.entity;

import com.nbro.domain.common.AppEnums;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stores the AI risk assessment result for a release.
 * One record per POST request — a new assessment is created each time
 * the risk endpoint is called, preserving the history.
 */
@Entity
@Table(name = "ai_risk_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The release this assessment belongs to */
    @Column(nullable = false)
    private UUID releaseId;

    /** LOW, MEDIUM, or HIGH */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppEnums.RiskLevel riskLevel;

    /** Why the AI gave this risk level */
    @Column(columnDefinition = "TEXT")
    private String reasoning;

    /** Concrete steps the team should take to mitigate risk */
    @Column(columnDefinition = "TEXT")
    private String suggestions;

    /** When this assessment was generated */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    /** Which AI model produced this assessment */
    @Column(nullable = false)
    private String modelUsed;
}
