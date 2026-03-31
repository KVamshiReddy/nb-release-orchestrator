package com.nbro.repository;

import com.nbro.domain.entity.AiRiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiRiskAssessmentRepository extends JpaRepository<AiRiskAssessment, UUID> {

    /**
     * Returns the most recent risk assessment for a release.
     * Ordered by generatedAt descending so the latest is first.
     */
    Optional<AiRiskAssessment> findTopByReleaseIdOrderByGeneratedAtDesc(UUID releaseId);
}
