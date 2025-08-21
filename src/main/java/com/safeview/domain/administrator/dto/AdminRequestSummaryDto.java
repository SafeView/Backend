package com.safeview.domain.administrator.dto;

import com.safeview.domain.administrator.entity.AdminRequestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/*
 * 관리자 권한 요청 요약 DTO
 * 
 * 사용자용 권한 요청 목록 조회 시 사용하는 DTO
 */
@Getter
@Setter
public class AdminRequestSummaryDto {
    private Long id;
    private String title;
    private AdminRequestStatus status;
    private LocalDateTime createdAt;
} 