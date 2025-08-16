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

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // ✅ CSRF 비활성화 (JWT는 상태를 저장하지 않음)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ✅ 세션 사용 안함
                )

                .authorizeHttpRequests(auth -> auth
                        // ✅ 인증 없이 허용할 API 경로
                        .requestMatchers("/api/auth/**").permitAll() // ex: /api/auth/login, signup 등
                        .requestMatchers("/api/users/**").permitAll()

                        // AI 서버 키 검증 엔드포인트 허용
                        .requestMatchers("/api/decryption/keys/verify/ai").permitAll()
                        .requestMatchers("/api/videos/make-entity").permitAll()

                        // ✅ 관리자 API는 ADMIN 권한 필요
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ✅ decryption API는 인증 필수
                        .requestMatchers("/api/decryption/**").authenticated()
                        .requestMatchers("/api/videos/**").authenticated()

                        // ✅ 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // ✅ 인증 실패 핸들러 등록
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // ✅ 커스텀 JWT 인증 필터 등록 (기존 UsernamePasswordAuthenticationFilter 앞에)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    // ✅ AuthenticationManager Bean 등록 (로그인 시 사용할 예정)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
