package com.safeview.domain.decryption.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class KeyVerificationResponseDto {

    // ✅ 검증 결과 (필수)
    private boolean isValid;               // 키 유효성 (필수)
    private String message;                // 검증 결과 메시지 (필수)
    private boolean canDecrypt;            // 복호화 권한 (필수)
    
    // ⏰ 시간 정보
    private LocalDateTime expiresAt;       // 만료 시간 (필수)
    private LocalDateTime verifiedAt;      // 검증 시간 (필수)
    
    // 🔐 보안 정보
    private String decryptionToken;        // 복호화용 임시 토큰 (유효한 경우만)
    private int remainingUses;             // 남은 사용 횟수
    
    // 📹 CCTV 정보
    private String cameraId;               // CCTV 카메라 ID
    private String location;               // 위치 정보
    
    // 🔗 블록체인 정보
    private String blockchainTxHash;       // 블록체인 트랜잭션 해시
    private boolean blockchainVerified;    // 블록체인 검증 상태
} 