package com.safeview.domain.decryption.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class KeyIssuanceResponseDto {

    // 🔐 보안 토큰 (실제 복호화키는 포함하지 않음)
    private String accessToken;                 // 일회성 접근 토큰 (안전)
    
    // ⏰ 시간 정보
    private LocalDateTime expiresAt;           // 만료 시간 (필수)
    private LocalDateTime issuedAt;            // 발급 시간 (필수)
    
    // 🔗 블록체인 정보
    private String blockchainTxHash;           // 블록체인 트랜잭션 해시 (검증용)
    private String keyHash;                    // 키 해시 (블록체인 검증용)
    
    // 📋 키 정보 (메타데이터만)
    private String keyType;                    // 키 타입 (CCTV_AES256)
    private String keyStatus;                  // 키 상태 (ACTIVE)
    private Long keyId;                        // 키 ID (내부 식별자)
    
    // 🔢 사용 횟수 정보
    private Integer remainingUses;             // 남은 사용 횟수
    private Integer totalUses;                 // 총 사용 횟수
    private Integer usedCount;                 // 사용된 횟수
    


} 