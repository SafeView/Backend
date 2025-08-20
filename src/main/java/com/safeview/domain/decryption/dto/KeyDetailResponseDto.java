package com.safeview.domain.decryption.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/*
 * 복호화 키 상세 정보 응답 DTO
 * 
 * 복호화 키의 상세 정보를 조회할 때 사용하는 DTO
 * 키의 메타데이터, 상태, 블록체인 정보 등을 포함
 */
@Getter
@Builder
public class KeyDetailResponseDto {

    private Long keyId;
    private Long userId;
    private String keyType;
    private String status;
    private String keyHash;
    private String blockchainTxHash;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private String revocationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 