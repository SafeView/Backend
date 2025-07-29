package com.safeview.domain.decryption.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class KeyListResponseDto {

    private List<KeySummaryDto> keys;
    private int totalCount;
    private int pageNumber;
    private int pageSize;

    @Getter
    @Builder
    public static class KeySummaryDto {
        private Long keyId;
        private Long userId;
        private String keyType;
        private String status;
        private String keyHash;
        private LocalDateTime issuedAt;
        private LocalDateTime expiresAt;
    }
} 