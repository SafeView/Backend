package com.safeview.global.security;

import com.safeview.domain.user.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 🔐 application.yml에서 주입될 시크릿 키
    @Value("${jwt.secret}")
    private String secretKey;

    // 🕓 토큰 만료 시간 (ms 단위)
    @Value("${jwt.expiration}")
    private long expirationTime;

    private Key key;

    // ✅ Base64 인코딩된 키로 변환 (객체 초기화 시)
    @PostConstruct
    protected void init() {
        byte[] decodedKey = Base64.getEncoder().encode(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    /**
     * ✅ 토큰 생성
     * @param userId 사용자 식별자
     * @param role 사용자 역할 (예: ROLE_USER)
     * @return 생성된 JWT 문자열
     */
    public String generateToken(Long userId, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 사용자 ID 저장
                .claim("role", role)               // 권한 클레임 추가
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘 및 키 지정
                .compact();
    }

    /**
     * ✅ 토큰에서 사용자 ID 추출
     */
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    /**
     * ✅ 토큰에서 Role 추출
     */
    public String getRoleFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }


    public List<SimpleGrantedAuthority> getAuthorities(String role) {
        return List.of(new SimpleGrantedAuthority(role));
    }

    /**
     * ✅ 토큰 유효성 검증
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
}
