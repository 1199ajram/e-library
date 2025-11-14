package Ziaat.E_library.Utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserContextUtil {

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    public String getCurrentToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? (String) auth.getCredentials() : null;
    }

    public Integer getCurrentUserId() {
        Map<String, Object> details = getUserDetails();
        return details != null ? (Integer) details.get("userId") : null;
    }

    public String getCurrentUserRole() {
        Map<String, Object> details = getUserDetails();
        return details != null ? (String) details.get("roleName") : null;
    }

    public Integer getCurrentUserAccountId() {
        Map<String, Object> details = getUserDetails();
        return details != null ? (Integer) details.get("userAccountId") : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof Map) {
            return (Map<String, Object>) auth.getDetails();
        }
        return null;
    }
}