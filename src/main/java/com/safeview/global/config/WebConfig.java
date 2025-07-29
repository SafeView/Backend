package com.safeview.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
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