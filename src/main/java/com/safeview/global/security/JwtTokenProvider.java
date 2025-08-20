package com.safeview.global.security;

import com.safeview.domain.user.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

/*
 * JWT 토큰 제공자 클래스
 * 
 * JWT 토큰의 생성, 검증, 파싱을 담당하는 클래스
 * Access Token과 Refresh Token을 관리하며 쿠키 기반 토큰 처리
 */
@Component
public class JwtTokenProvider {

    /*
     * JWT 시크릿 키 (application.yml에서 주입)
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /*
     * Access Token 만료 시간 (ms 단위, 기본 1시간)
     */
    @Value("${jwt.expiration}")
    private long accessTokenExpirationTime;

    /*
     * Refresh Token 만료 시간 (ms 단위, 기본 7일)
     */
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpirationTime;

    /*
     * JWT 서명에 사용할 키
     */
    private Key key;

    /*
     * JWT 키 초기화
     * 
     * Base64 인코딩된 키로 변환하여 JWT 서명에 사용할 키 설정
     */
    @PostConstruct
    protected void init() {
        byte[] decodedKey = Base64.getEncoder().encode(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    /*
     * Access Token 생성
     * 
     * @param userId 사용자 식별자
     * @param role 사용자 역할
     * @return 생성된 JWT 문자열
     * 
     * 기능: 사용자 ID, 역할, 토큰 타입을 포함한 Access Token 생성
     * 만료 시간: 1시간
     */
    public String generateAccessToken(Long userId, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationTime);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 사용자 ID 저장
                .claim("role", role.name())        // Role enum의 name() 사용
                .claim("type", "ACCESS")           // 토큰 타입 구분
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘 및 키 지정
                .compact();
    }

    /*
     * Refresh Token 생성
     * 
     * @param userId 사용자 식별자
     * @return 생성된 Refresh Token 문자열
     * 
     * 기능: 사용자 ID와 토큰 타입을 포함한 Refresh Token 생성
     * 만료 시간: 7일
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationTime);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 사용자 ID 저장
                .claim("type", "REFRESH")           // 토큰 타입 구분
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /*
     * 토큰에서 사용자 ID 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    /*
     * 토큰에서 Role 추출
     * 
     * @param token JWT 토큰
     * @return ROLE_ 접두사가 추가된 역할 문자열
     */
    public String getRoleFromToken(String token) {
        String roleName = (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
        
        // ROLE_ 접두사 추가
        return "ROLE_" + roleName;
    }

    /*
     * 토큰 타입 확인
     * 
     * @param token JWT 토큰
     * @return 토큰 타입 (ACCESS 또는 REFRESH)
     */
    public String getTokenType(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("type");
    }

    /*
     * Refresh Token 여부 확인
     * 
     * @param token JWT 토큰
     * @return Refresh Token인지 여부
     */
    public boolean isRefreshToken(String token) {
        try {
            String tokenType = getTokenType(token);
            return "REFRESH".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Access Token 여부 확인
     * 
     * @param token JWT 토큰
     * @return Access Token인지 여부
     */
    public boolean isAccessToken(String token) {
        try {
            String tokenType = getTokenType(token);
            return "ACCESS".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * 권한 목록 생성
     * 
     * @param role 역할 문자열
     * @return SimpleGrantedAuthority 목록
     */
    public List<SimpleGrantedAuthority> getAuthorities(String role) {
        return List.of(new SimpleGrantedAuthority(role));
    }

    /*
     * 토큰 유효성 검증
     * 
     * @param token JWT 토큰
     * @return 토큰이 유효한지 여부
     * 
     * 검증 항목: 만료 여부, 서명 유효성, 형식 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원하지 않는 JWT 토큰입니다.");
        } catch (MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims 문자열이 비었습니다.");
        } catch (SecurityException e) {
            System.out.println("JWT 서명 검증에 실패했습니다.");
        }
        return false;
    }

    /*
     * 쿠키에서 Access Token 추출
     * 
     * @param request HTTP 요청 객체
     * @return Access Token 문자열 (Bearer 접두사 제거)
     */
    public String resolveTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                String value = cookie.getValue();
                return value.startsWith("Bearer ") ? value.substring(7) : value;
            }
        }
        return null;
    }

    /*
     * 쿠키에서 Refresh Token 추출
     * 
     * @param request HTTP 요청 객체
     * @return Refresh Token 문자열 (Bearer 접두사 제거)
     */
    public String resolveRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                String value = cookie.getValue();
                return value.startsWith("Bearer ") ? value.substring(7) : value;
            }
        }
        return null;
    }
}