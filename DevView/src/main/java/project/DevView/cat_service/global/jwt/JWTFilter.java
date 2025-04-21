package project.DevView.cat_service.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import project.DevView.cat_service.user.dto.CustomUserDetails;
import project.DevView.cat_service.user.entity.UserEntity;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 이 경로들은 토큰 검증 자체를 건너뛴다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/api/v1/login")
                || path.equals("/api/v1/join")
                || path.equals("/joinPage")
                || path.startsWith("/api/v1/img/")
                || path.startsWith("/css/")
                || path.startsWith("/js/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("[JWTFilter] Request URI: " + request.getRequestURI());

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String token = null;
            for (Cookie c : cookies) {
                if ("Auth".equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }

            if (token != null && !jwtUtil.isExpired(token)) {
                // (1) 토큰 파싱
                int userId = jwtUtil.getId(token);
                String username = jwtUtil.getUsername(token);
                String role = jwtUtil.getRole(token);

                System.out.println("[JWTFilter] Token valid. userId=" + userId
                        + ", username=" + username + ", role=" + role);

                // (2) SecurityContext 설정
                UserEntity userEntity = new UserEntity();
                userEntity.setId(userId);
                userEntity.setUsername(username);
                userEntity.setRole(role);

                CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
                Authentication authToken = new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("[JWTFilter] No valid JWT token found -> anonymous");
            }
        } else {
            System.out.println("[JWTFilter] No cookies found -> anonymous");
        }

        filterChain.doFilter(request, response);
    }
}
