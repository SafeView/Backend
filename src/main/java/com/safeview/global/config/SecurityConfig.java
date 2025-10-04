package com.safeview.global.config;

import com.safeview.global.security.JwtAuthenticationFilter;
import com.safeview.global.security.JwtAuthenticationEntryPoint;
import com.safeview.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * 보안 설정 클래스
 * 
 * Spring Security 설정을 담당하는 클래스
 * JWT 기반 인증, 권한별 접근 제어, 보안 필터 설정을 포함
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /*
     * 보안 필터 체인 설정
     * 
     * HTTP 요청에 대한 보안 정책을 설정
     * CSRF 비활성화, 세션 정책, 권한별 접근 제어, JWT 필터 등록
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (JWT는 상태를 저장하지 않음)

                .cors(cors -> {}) // WebConfig의 CORS 설정을 SecurityFilterChain에 반영

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안함
                )

                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 허용할 API 경로
                        .requestMatchers("/api/auth/**").permitAll() // ex: /api/auth/login, signup 등
                        .requestMatchers("/api/users/**").permitAll()

                        // AI 서버 키 검증 엔드포인트 허용
                        .requestMatchers("/api/decryption/keys/verify/ai").permitAll()
                        .requestMatchers("/api/videos/make-entity").permitAll()

                        // 관리자 API는 인증만 필요
                        .requestMatchers("/api/admin/**").authenticated()

                        // decryption API는 인증 필수
                        .requestMatchers("/api/decryption/**").authenticated()
                        .requestMatchers("/api/videos/**").authenticated()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 인증 실패 핸들러 등록
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // 커스텀 JWT 인증 필터 등록 (기존 UsernamePasswordAuthenticationFilter 앞에)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /*
     * 인증 매니저 Bean 등록
     * 
     * 로그인 시 사용자 인증을 처리하는 AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /*
     * 비밀번호 인코더 Bean 등록
     * 
     * 비밀번호 암호화를 위한 BCrypt 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
