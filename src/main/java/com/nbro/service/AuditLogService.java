package com.nbro.service;

import com.nbro.domain.common.AppEnums;
import com.nbro.domain.entity.AuditLog;
import com.nbro.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public List<AuditLog> getAuditLogsForRelease(UUID releaseId) {
        return auditLogRepository.findByReleaseIDOrderByChangedAtAsc(releaseId);
    }

    public void logStatusChange(UUID releaseId,
                                String changedBy,
                                AppEnums.ReleaseStatus fromStatus,
                                AppEnums.ReleaseStatus toStatus) {
        AuditLog log = AuditLog.builder()
                .releaseID(releaseId)
                .changedBy(changedBy)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .build();
        auditLogRepository.save(log);
    }

}
