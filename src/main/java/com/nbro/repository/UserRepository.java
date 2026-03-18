package com.nbro.repository;

import com.nbro.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This interface handles all database operations for the User table.
 * If Mongo, we extend the MongoRepository instead.
 * By extending JpaRepository, we get basic operations for free —
 * save, findById, findAll, delete etc. without writing any SQL.
 * <p>
 * Spring Data JPA automatically generates the actual database queries
 * based on the method names we define here.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their email address, ignoring upper or lower case.
     *
     * @param email - the email address to search for
     * @return the user wrapped in Optional, or empty Optional if not found
     */
    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findByIsActiveTrue();

    boolean existsByEmailIgnoreCase(String email);

}