package com.safeview.domain.decryption.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "blockchain")
public class BlockchainConfig {

    /**
     * RPC URL (예: https://mainnet.infura.io/v3/YOUR_PROJECT_ID)
     */
    private String rpcUrl;
    
    /**
     * 스마트 컨트랙트 주소
     */
    private String contractAddress;
    
    /**
     * 개인키 (환경변수로 관리 권장)
     */
    private String privateKey;
    
    /**
     * 가스 가격 (Wei)
     */
    private String gasPrice = "20000000000";
    
    /**
     * 가스 한도
     */
    private String gasLimit = "300000";
    
    /**
     * 네트워크 ID (1: Ethereum Mainnet, 11155111: Sepolia Testnet)
     */
    private Long networkId = 11155111L;
    
    /**
     * 블록체인 활성화 여부
     */
    private boolean enabled = true;
    
    /**
     * 트랜잭션 확인 대기 시간 (초)
     */
    private int confirmationTimeout = 60;
    
    /**
     * 최대 재시도 횟수
     */
    private int maxRetries = 3;
    
    /**
     * 재시도 간격 (밀리초)
     */
    private int retryInterval = 1000;
    
    /**
     * Sepolia 테스트넷 여부
     */
    private boolean testnet = true;
    
    /**
     * 시뮬레이션 모드 (실제 블록체인 연동 전까지 활성화)
     */
    private boolean simulationMode = true;
} 