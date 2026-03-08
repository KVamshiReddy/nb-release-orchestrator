package com.nbro.domain.common;

/**
 * This class consists of all the enums we will use in
 * any of the modules in the entire project.
 */

public class AppEnums {
    public enum Role {
        DEV,
        CROSS_DEV,
        DEVOPS,
        SCRUM_MASTER,
        PRODUCT_OWNER,
        RELEASE_MANAGER,
        STAKEHOLDER
    }

    public enum ReleaseType {
        MAJOR,
        MINOR,
        HOTFIX
    }

    public enum TargetEnvironment {
        PRODUCTION,
        STAGING,
        DEVELOPMENT
    }

    public enum ReleaseStatus {
        DRAFT,
        PENDING_REVIEW,
        CAB_REVIEW,
        APPROVED,
        IN_PROGRESS,
        DEPLOYED,
        VERIFIED,
        ROLLED_BACK,
        REJECTED
    }


}
