package com.safeview.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 * JWT 인증 필터 클래스
 * 
 * 모든 HTTP 요청에 대해 JWT 토큰을 검증하는 필터
 * 헤더 또는 쿠키에서 토큰을 추출하여 인증 처리
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /*
     * JWT 토큰 제공자 주입을 위한 생성자
     */
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /*
     * JWT 인증 필터 처리
     * 
     * 모든 HTTP 요청에 대해 JWT 토큰을 검증하고 인증 처리
     * 특정 경로는 인증 없이 통과 허용
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 인증 없이 통과할 경로들
        String uri = request.getRequestURI();
        System.out.println("JwtFilter URI: " + uri);
        if (uri.startsWith("/api/auth/") || 
            uri.endsWith("/api/videos/make-entity") ||
            uri.equals("/api/decryption/keys/verify/ai")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Authorization 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰이 존재하고 유효하면
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 사용자 ID와 Role 추출
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);

            // 3. 인증 객체 생성 (여기선 비밀번호 없이 인증만 함)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId, // Principal로 userId 사용
                            null,   // credentials는 null
                            jwtTokenProvider.getAuthorities(role) // 권한 목록
                    );

            // 요청에 대한 상세 정보 저장
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 4. SecurityContext에 인증 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /*
     * 요청에서 JWT 토큰 추출
     * 
     * Authorization 헤더 또는 쿠키에서 JWT 토큰을 추출
     * Bearer 접두사가 있는 경우 제거하여 반환
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // "Bearer eyJ..." 형식에서 "Bearer " 제거
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        if(request.getCookies()!= null) {
            for(jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if("accessToken".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}