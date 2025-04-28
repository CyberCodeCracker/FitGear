package com.amouri_coding.FitGear.common;

import com.amouri_coding.FitGear.user.coach.Coach;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

@Slf4j
public class SecurityUtils {

    private SecurityUtils() {

    }

    // Verifies that the Authentication object is a coach
    public static Coach getAuthenticatedAndVerifiedCoach(Authentication authentication) {

        if (authentication == null) {
            log.error("No authentication found");
            throw new AccessDeniedException("Authentication required");
        }

        if (authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_COACH"))) {
            log.error("You are not a coach");
            throw new AccessDeniedException("You are not a coach");
        }

        Coach connectedCoach = (Coach) authentication.getPrincipal();

        if (connectedCoach == null) {
            throw new IllegalStateException("Coach not found");
        }

        if (!connectedCoach.isVerified()) {
            throw new AccessDeniedException("You are not verified");
        }

        return connectedCoach;
    }
}
