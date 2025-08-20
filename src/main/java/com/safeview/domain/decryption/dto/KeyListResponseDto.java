package com.safeview.domain.decryption.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/*
 * 복호화 키 목록 응답 DTO
 * 
 * 복호화 키 목록을 조회할 때 사용하는 DTO
 * 페이징 정보와 키 요약 정보를 포함
 */
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