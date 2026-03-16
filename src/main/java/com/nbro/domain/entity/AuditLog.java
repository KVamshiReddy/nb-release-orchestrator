package com.nbro.domain.entity;

import com.nbro.domain.common.AppEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false)
    private UUID id;
    @Column(updatable = false)
    private UUID releaseID;
    @Column(updatable = false)
    private String changedBy;
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private AppEnums.ReleaseStatus fromStatus;
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private AppEnums.ReleaseStatus toStatus;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime changedAt;
}
