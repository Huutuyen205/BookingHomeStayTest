package BookingHomeStay.BookingHomeStay.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {

        // Neu nguoi dung bi chan boi Spring Security khi dang truy cap 1 trang can dang nhap
        // (vd: bam "Dat phong" luc chua login) thi uu tien quay lai dung trang do sau khi login thanh cong.
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            clearAuthenticationAttributes(request);
            getRedirectStrategy().sendRedirect(request, response, savedRequest.getRedirectUrl());
            return;
        }

        // Khong co trang nao duoc luu lai (nguoi dung vao thang /login) -> dieu huong theo vai tro
        String redirectUrl = "/";
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) { redirectUrl = "/quan-tri/tong-quan"; break; }
            if (role.equals("ROLE_HOST") || role.equals("ROLE_COLLABORATOR")) { redirectUrl = "/chu-nha/tong-quan"; break; }
        }
        clearAuthenticationAttributes(request);
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}