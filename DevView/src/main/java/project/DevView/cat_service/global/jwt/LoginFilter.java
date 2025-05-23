package project.DevView.cat_service.global.jwt;

import project.DevView.cat_service.user.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        super.setFilterProcessesUrl("/api/v1/login");
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println("[LoginFilter] attemptAuthentication -> username=" + username + ", password=" + (password != null ? "****" : "null"));

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication
    ) throws IOException {
        System.out.println("[LoginFilter] successfulAuthentication -> user=" + authentication.getName());

        // (1) UserDetails에서 username, role, userId 추출
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        long userId = customUserDetails.getId(); // DB PK
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        Iterator<? extends GrantedAuthority> it = authorities.iterator();
        String role = "ROLE_USER";
        if (it.hasNext()) {
            role = it.next().getAuthority();
        }

        // (2) JWT 토큰 생성 (10시간)
        long expirationSeconds = 60L * 60L * 10L;
        String token = jwtUtil.createJwt(userId, username, role, expirationSeconds);

        System.out.println("[LoginFilter] JWT Token Created: " + token);

        // (3) ResponseCookie - 쿠키 설정 개선
        ResponseCookie jwtCookie = ResponseCookie.from("Auth", token)
                .httpOnly(true)
                .secure(false)    // 로컬 http
                .path("/")       // 모든 경로에서 접근 가능하도록 설정
                .maxAge(expirationSeconds)
                .sameSite("Lax") // Lax 설정 유지
                .build();

        System.out.println("[LoginFilter] ResponseCookie: " + jwtCookie);

        // (4) 응답 헤더에 Set-Cookie
        String setCookieValue = jwtCookie.toString();
        response.setHeader(HttpHeaders.SET_COOKIE, setCookieValue);
        System.out.println("[LoginFilter] Set-Cookie: " + setCookieValue);

        // (5) redirect 파라미터 확인 및 리다이렉트
        String redirectUrl = request.getParameter("redirect");
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect("/interview-mode");
        }
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) {
        System.out.println("[LoginFilter] unsuccessfulAuthentication -> " + failed.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
