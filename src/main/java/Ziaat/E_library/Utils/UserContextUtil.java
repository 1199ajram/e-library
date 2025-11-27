package Ziaat.E_library.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class UserContextUtil {

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }
        return auth.getName();
    }

    public String getCurrentToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }

        if (auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();
            return jwt.getTokenValue();
        }
        return auth.getCredentials() != null ? auth.getCredentials().toString() : null;
    }

    public Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication exists and has a principal
        if (auth == null || auth.getPrincipal() == null) {
            log.warn("No authentication or principal found");
            return null;
        }

        // Check if principal is anonymous
        if ("anonymousUser".equals(auth.getPrincipal())) {
            log.warn("Anonymous user detected");
            return null;
        }

        // Try to get from JWT token claims
        if (auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            // Try different claim names
            Object userId = jwt.getClaim("userId");
            if (userId == null) userId = jwt.getClaim("user_id");
            if (userId == null) userId = jwt.getClaim("sub");
            if (userId == null) userId = jwt.getClaim("id");

            if (userId != null) {
                return parseToInteger(userId);
            }
        }

        // Try to get from details
        Map<String, Object> details = getUserDetails();
        if (details != null && details.containsKey("userId")) {
            return parseToInteger(details.get("userId"));
        }

        // Try to parse from username if it's a number
        try {
            String name = auth.getName();
            if (name != null && !name.isEmpty()) {
                return Integer.parseInt(name);
            }
        } catch (NumberFormatException e) {
            log.error("Unable to extract userId. Principal type: {}, Name: {}",
                    auth.getPrincipal().getClass().getName(), auth.getName());
        }

        log.error("Could not extract userId from authentication");
        return null;
    }

    public String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }

        // Try to get from JWT token claims
        if (auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            Object role = jwt.getClaim("roleName");
            if (role == null) role = jwt.getClaim("role");
            if (role == null) role = jwt.getClaim("authorities");

            if (role != null) {
                return role.toString();
            }
        }

        // Try from authorities
        if (auth.getAuthorities() != null && !auth.getAuthorities().isEmpty()) {
            return auth.getAuthorities().iterator().next().getAuthority();
        }

        // Try from details
        Map<String, Object> details = getUserDetails();
        return details != null ? (String) details.get("roleName") : null;
    }

    public Integer getCurrentUserAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }

        // Try to get from JWT token claims
        if (auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            Object accountId = jwt.getClaim("userAccountId");
            if (accountId == null) accountId = jwt.getClaim("accountId");

            if (accountId != null) {
                return parseToInteger(accountId);
            }
        }

        // Try from details
        Map<String, Object> details = getUserDetails();
        if (details != null && details.containsKey("userAccountId")) {
            return parseToInteger(details.get("userAccountId"));
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof Map) {
            return (Map<String, Object>) auth.getDetails();
        }
        return null;
    }

    private Integer parseToInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                log.error("Failed to parse value to Integer: {}", value);
                return null;
            }
        }
        log.error("Unexpected type for integer value: {}", value.getClass().getName());
        return null;
    }

    /**
     * Debug method to print authentication details
     */
    public void debugAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("=== AUTHENTICATION DEBUG ===");

        if (auth == null) {
            System.out.println("Authentication is NULL");
            return;
        }

        System.out.println("Authentication exists: " + auth.getClass().getName());
        System.out.println("Is authenticated: " + auth.isAuthenticated());
        System.out.println("Name: " + auth.getName());

        Object principal = auth.getPrincipal();
        if (principal == null) {
            System.out.println("Principal is NULL");
        } else {
            System.out.println("Principal class: " + principal.getClass().getName());
            System.out.println("Principal value: " + principal);

            if (principal instanceof Jwt) {
                Jwt jwt = (Jwt) principal;
                System.out.println("JWT Claims:");
                jwt.getClaims().forEach((key, value) ->
                        System.out.println("  " + key + " = " + value + " (" + (value != null ? value.getClass().getName() : "null") + ")")
                );
            }
        }

        System.out.println("Details: " + auth.getDetails());
        System.out.println("Authorities: " + auth.getAuthorities());
        System.out.println("Credentials: " + (auth.getCredentials() != null ? "EXISTS" : "NULL"));
        System.out.println("===========================");
    }
}