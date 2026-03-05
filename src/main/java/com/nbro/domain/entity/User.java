package com.nbro.domain.entity;

import com.nbro.domain.common.AppEnums;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a user in the NB Release Orchestrator system.
 *
 * This is the database table "users" mapped to a Java class.
 * Every person who uses the portal has a record in this table —
 * developers, DevOps engineers, release managers, stakeholders etc.
 *
 * Each user has a role that controls what they can see and do
 * in the system. For example, a ROLE_DEV can create releases
 * but only a ROLE_RELEASE_MANAGER can approve them.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class User {

    /**
     * The unique identifier for this user.
     * Automatically generated as a UUID when a new user is created.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The user's email address.
     * Used as the username for logging in.
     * Must be unique — no two users can have the same email.
     * Cannot be null or empty.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The user's hashed password.
     * Cannot be null or empty.
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * The user's full name
     * Cannot be null or empty.
     */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /**
     * The user's role in the system.
     * This controls what actions the user can perform.
     * Stored as a string in the database (e.g. "ROLE_DEV")
     * instead of a number so it is human-readable in the database.
     *
     * Available roles are defined in AppEnums.Role:
     * ROLE_DEV, ROLE_CROSS_DEV, ROLE_DEVOPS, ROLE_SCRUM_MASTER,
     * ROLE_PRODUCT_OWNER, ROLE_RELEASE_MANAGER, ROLE_STAKEHOLDER
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppEnums.Role role;

    /**
     * The team this user belongs to.
     * Example: "Backend Team", "DevOps Team"
     * This field is optional — a user can exist without a team.
     */
    private String team;

    /**
     * Whether this user account is active or not.
     * Active users (true) can log in and use the system.
     * Inactive users (false) are essentially soft deleted —
     * their data is kept, but they cannot log in.
     * Defaults to true when a new user is created.
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /**
     * The date and time when this user account was created.
     * Automatically set by Hibernate when the record is first saved.
     * Cannot be changed after creation (updatable = false).
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The date and time when this user account was last updated.
     * Automatically updated by Hibernate every time the record changes.
     * Useful for auditing — knowing when a user's role or details changed.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}