package com.nbro.repository;

import com.nbro.domain.common.AppEnums;
import com.nbro.domain.entity.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles all database operations for the releases table.
 * Extends JpaRepository which gives us save, findById, findAll,
 * delete and more for free without writing any SQL.
 */
@Repository
public interface ReleaseRepository extends JpaRepository<Release, UUID> {

    /**
     * Finds a release by its Jira key, ignoring upper or lower case.
     */
    Optional<Release> findByJiraKeyIgnoreCase(String key);

    /**
     * Returns a list of [status, count] pairs for the dashboard stats card.
     */
    @Query("SELECT r.releaseStatus, COUNT(r) FROM Release r GROUP BY r.releaseStatus")
    List<Object[]> countGroupedByStatus();

    /**
     * Returns all releases that have a scheduled date (for the calendar view),
     * ordered by scheduled date ascending.
     */
    List<Release> findAllByScheduledReleaseDateIsNotNullOrderByScheduledReleaseDateAsc();
}