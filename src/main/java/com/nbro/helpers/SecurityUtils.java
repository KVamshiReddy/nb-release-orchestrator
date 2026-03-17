package com.nbro.helpers;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getLoggedInUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        return authentication.getName();
    }
}
