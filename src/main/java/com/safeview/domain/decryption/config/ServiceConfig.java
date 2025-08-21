package com.safeview.domain.decryption.config;

import com.safeview.domain.decryption.service.BlockchainService;
import com.safeview.domain.decryption.service.RealBlockchainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/*
 * 서비스 설정 클래스
 * 
 * 복호화 관련 서비스 빈을 설정하는 클래스
 * 블록체인 서비스 구현체를 등록
 */
@Configuration
public class ServiceConfig {

    @Bean
    @Primary
    public BlockchainService blockchainService() {
        return new RealBlockchainServiceImpl();
    }
} 