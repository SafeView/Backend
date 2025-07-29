package com.safeview.domain.decryption.config;

import com.safeview.domain.decryption.service.BlockchainService;
import com.safeview.domain.decryption.service.RealBlockchainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ServiceConfig {

    @Bean
    @Primary
    public BlockchainService blockchainService() {
        return new RealBlockchainServiceImpl();
    }
} 