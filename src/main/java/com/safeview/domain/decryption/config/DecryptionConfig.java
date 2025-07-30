package com.safeview.domain.decryption.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cctv.decryption")
public class DecryptionConfig {

    /**
     * 키 설정
     */
    private KeyConfig key = new KeyConfig();
    
    /**
     * 보안 설정
     */
    private SecurityConfig security = new SecurityConfig();
    
    /**
     * 블록체인 설정
     */
    private BlockchainConfig blockchain = new BlockchainConfig();

    @Getter
    @Setter
    public static class KeyConfig {
        /**
         * 키 만료 일수 (기본값: 30일)
         */
        private int expirationDays = 30;
        
        /**
         * 키 타입 (기본값: CCTV_AES256)
         */
        private String type = "CCTV_AES256";
        
        /**
         * 키 크기 (바이트, 기본값: 32)
         */
        private int size = 32;
        
        /**
         * 기본 사용 횟수 (기본값: 90회 - 30일간 하루 3회 사용 가정)
         */
        private int defaultUses = 90;
    }

    @Getter
    @Setter
    public static class SecurityConfig {
        /**
         * 기본 보안 레벨 (LOW, MEDIUM, HIGH)
         */
        private String defaultLevel = "MEDIUM";
        
        /**
         * 토큰 크기 (바이트, 기본값: 32)
         */
        private int tokenSize = 32;
        
        /**
         * 검증 코드 길이 (기본값: 6)
         */
        private int verificationCodeLength = 6;
        
        /**
         * 생체 인증 활성화 여부
         */
        private boolean biometricEnabled = false;
        
        /**
         * 디바이스 지문 활성화 여부
         */
        private boolean deviceFingerprintEnabled = false;
    }

    @Getter
    @Setter
    public static class BlockchainConfig {
        /**
         * 블록체인 활성화 여부
         */
        private boolean enabled = true;
        
        /**
         * 트랜잭션 확인 대기 시간 (초)
         */
        private int confirmationTimeout = 60;
        
        /**
         * 가스 가격 (Wei)
         */
        private String gasPrice = "20000000000";
        
        /**
         * 가스 한도
         */
        private String gasLimit = "300000";
    }
} 