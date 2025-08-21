package com.safeview.domain.decryption.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/*
 * 복호화 키 검증 응답 DTO
 * 
 * 복호화 키 검증 결과를 클라이언트에게 반환하는 정보
 * 키의 유효성, 복호화 권한, 사용 가능 여부 등을 포함
 */
@Getter
@Builder
public class KeyVerificationResponseDto {

    // 검증 결과 (필수)
    private boolean isValid;               // 키 유효성 (필수)
    private String message;                // 검증 결과 메시지 (필수)
    private boolean canDecrypt;            // 복호화 권한 (필수)
    
    // 시간 정보
    private LocalDateTime expiresAt;       // 만료 시간 (필수)
    private LocalDateTime verifiedAt;      // 검증 시간 (필수)
    
    // 보안 정보
    private String decryptionToken;        // 복호화용 임시 토큰 (유효한 경우만)
    private int remainingUses;             // 남은 사용 횟수
    
    // CCTV 정보
    private String cameraId;               // CCTV 카메라 ID
    
    // 블록체인 정보
    private String blockchainTxHash;       // 블록체인 트랜잭션 해시
    private boolean blockchainVerified;    // 블록체인 검증 상태
} 