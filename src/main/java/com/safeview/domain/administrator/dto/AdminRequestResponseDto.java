package com.safeview.domain.administrator.dto;

import com.safeview.domain.administrator.entity.AdminRequestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/*
 * 관리자 권한 요청 응답 DTO
 * 
 * 관리자 권한 요청의 상세 정보를 클라이언트에게 반환하는 DTO
 */
@Getter
@Setter
public class AdminRequestResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private AdminRequestStatus status;
    private String adminComment;
    private LocalDateTime processedAt;
    private Long processedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 