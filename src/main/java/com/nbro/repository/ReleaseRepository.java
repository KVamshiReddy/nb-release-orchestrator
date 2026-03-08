package com.nbro.repository;

import com.nbro.domain.entity.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * For example "rm-1" and "RM-1" will both find the same release.
     * <p>
     * Used to prevent duplicate releases for the same Jira issue.
     *
     * @param key - the Jira key to search for e.g. "RM-1"
     * @return the release wrapped in Optional, or empty if not found
     */
    Optional<Release> findByJiraKeyIgnoreCase(String key);
}