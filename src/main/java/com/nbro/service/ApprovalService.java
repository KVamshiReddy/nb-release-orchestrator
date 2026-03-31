package com.nbro.service;

import com.nbro.Exceptions.ErrorMessages;
import com.nbro.domain.common.AppEnums;
import com.nbro.domain.dto.ApprovalRequestDTO;
import com.nbro.domain.entity.Approval;
import com.nbro.helpers.SecurityUtils;
import com.nbro.repository.ApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalService.class);

    private final ApprovalRepository approvalRepository;
    private final ReleaseService releaseService;

    @Transactional
    public Approval submitApproval(ApprovalRequestDTO request) {
        if (request.getReleaseId() == null) {
            throw new IllegalArgumentException(ErrorMessages.DETAILS_NOT_VALID);
        }
        // Verify release exists — throws ResourceNotFoundException if not
        releaseService.getReleaseById(request.getReleaseId());

        String email = SecurityUtils.getLoggedInUserEmail();
        Approval approval = Approval.builder()
                .approvedBy(email)
                .approvalStatus(request.getApprovalStatus())
                .comments(request.getComments())
                .releaseId(request.getReleaseId())
                .build();
        if (request.getApprovalStatus() == AppEnums.ApprovalStatus.APPROVED) {
            releaseService.updateStatus(request.getReleaseId(), AppEnums.ReleaseStatus.APPROVED);
        } else {
            releaseService.updateStatus(request.getReleaseId(), AppEnums.ReleaseStatus.REJECTED);
        }
        Approval saved = approvalRepository.save(approval);
        logger.info("Approval submitted by {} for release {}: {}", email, request.getReleaseId(), request.getApprovalStatus());
        return saved;
    }

    public List<Approval> getApprovalsForRelease(UUID releaseId) {
        return approvalRepository.findByReleaseIdOrderByApprovedAtAsc(releaseId);
    }

}
