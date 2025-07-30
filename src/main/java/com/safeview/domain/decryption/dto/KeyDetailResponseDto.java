package com.safeview.domain.decryption.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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