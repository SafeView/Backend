package com.safeview.domain.administrator.dto;

import com.safeview.domain.administrator.entity.AdminRequestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/*
 * 관리자용 권한 요청 요약 DTO
 * 
 * 관리자가 권한 요청 목록을 조회할 때 사용하는 DTO
 */
@Getter
@Setter
public class AdminRequestSummaryForAdminDto {
    private Long id;
    private Long userId;
    private String title;
    private AdminRequestStatus status;
    private LocalDateTime createdAt;
} 