package com.example.cat_service.jwt;

import com.example.cat_service.user.dto.CustomUserDetails;
import com.example.cat_service.user.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("[JWTFilter] Request URI: " + request.getRequestURI());

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            System.out.println("[JWTFilter] No cookies found -> anonymous");
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;
        for (Cookie c : cookies) {
            System.out.println("[JWTFilter] Found cookie: " + c.getName() + " = " + c.getValue());
            if ("Auth".equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }

        if (token == null) {
            System.out.println("[JWTFilter] 'Auth' cookie not found -> anonymous");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 여부
        if (jwtUtil.isExpired(token)) {
            System.out.println("[JWTFilter] Token expired -> anonymous");
            filterChain.doFilter(request, response);
            return;
        }

        // (1) 토큰 파싱
        int userId = jwtUtil.getId(token);        // 추가
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        System.out.println("[JWTFilter] Token valid. userId=" + userId
                + ", username=" + username + ", role=" + role);

        // (2) SecurityContext 설정
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);        // DB PK
        userEntity.setUsername(username);
        userEntity.setRole(role);

        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails,
                null,
                customUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
