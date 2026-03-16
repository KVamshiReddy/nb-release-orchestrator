package com.nbro.helpers;

import com.nbro.domain.common.AppEnums;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nbro.domain.common.AppEnums.ReleaseStatus.*;

/**
 * Validates release workflow transitions.
 * <p>
 * Think of this as a traffic light system for releases — it controls
 * which status changes are allowed and which ones are not.
 */
@Component
public class ReleaseValidators {

    /**
     * A map that defines which status transitions are allowed.
     * Key = current status, Value = list of valid next statuses.
     * <p>
     * This is defined as a static constant so it is built once
     * when the application starts and reused for every validation —
     * no need to rebuild it on every method call.
     */
    private static final Map<AppEnums.ReleaseStatus,
            List<AppEnums.ReleaseStatus>> validFlows = new HashMap<>();

    static {
        validFlows.put(DRAFT, List.of(PENDING_REVIEW));
        validFlows.put(PENDING_REVIEW, List.of(CAB_REVIEW, APPROVED, REJECTED));
        validFlows.put(APPROVED, List.of(IN_PROGRESS));
        validFlows.put(CAB_REVIEW, List.of(APPROVED, REJECTED));
        validFlows.put(IN_PROGRESS, List.of(DEPLOYED, ROLLED_BACK));
        validFlows.put(DEPLOYED, List.of(VERIFIED));
        validFlows.put(ROLLED_BACK, List.of(PENDING_REVIEW));
    }

    /**
     * Validates whether a release can move from one status to another.
     * <p>
     * This method does nothing if the transition is valid.
     * It throws an exception if the transition is not allowed
     *
     * @param presentState  - the current status of the release
     * @param upcomingState - the status the release is trying to move to
     * @throws RuntimeException      if the current status is not recognized
     * @throws IllegalStateException if the transition is not allowed
     */
    public static void isValidWorkFlow(AppEnums.ReleaseStatus presentState,
                                       AppEnums.ReleaseStatus upcomingState) {

        // Check if the current status exists in our workflow map
        // VERIFIED and REJECTED are final states — they have no valid next steps
        if (!validFlows.containsKey(presentState)) {
            throw new RuntimeException("Invalid State Found");
        }

        // Get the list of allowed next statuses for the current status
        List<AppEnums.ReleaseStatus> values = validFlows.get(presentState);

        // Check if the requested next status is in the allowed list
        if (!values.contains(upcomingState)) {
            throw new IllegalStateException(
                    "Cannot transition from " + presentState + " to " + upcomingState
            );
        }
    }
}