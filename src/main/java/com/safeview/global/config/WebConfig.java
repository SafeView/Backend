package com.safeview.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * 웹 설정 클래스
 * 
 * 웹 관련 설정을 담당하는 클래스
 * CORS 설정, 웹 MVC 설정 등을 포함
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /*
     * CORS 설정
     * 
     * Cross-Origin Resource Sharing 설정을 통해 프론트엔드와의 통신 허용
     * 개발 환경의 React 서버와 Spring Boot 서버 간 통신을 위한 설정
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 요청에 대해
                .allowedOrigins(
                    "http://localhost:5173", // React dev 서버 주소
                    "http://localhost:8080", // Spring Boot 서버 주소 (정적 파일 접근용)
                    "http://127.0.0.1:8080"  // localhost 대체 주소
                )
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}