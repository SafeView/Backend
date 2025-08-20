package com.safeview.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/*
 * RestTemplate 설정 클래스
 * 
 * HTTP 클라이언트인 RestTemplate Bean을 등록하는 설정 클래스
 * AI 서버와의 REST API 통신에 사용
 */
@Configuration
public class RestTemplateConfig {

    /*
     * RestTemplate Bean 등록
     * 
     * HTTP 클라이언트 Bean을 등록하여 AI 서버와의 통신에 사용
     */
    @Bean
    public RestTemplate restTemplate() {return new RestTemplate();}
}
