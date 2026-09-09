package BookingHomeStay.BookingHomeStay.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private SecurityUtils() {}

    public static CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) return null;
        return (CustomUserDetails) auth.getPrincipal();
    }

    public static Long getCurrentUserId() {
        CustomUserDetails u = getCurrentUser();
        return u != null ? u.getId() : null;
    }
}