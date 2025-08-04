package com.safeview.domain.administrator.service;

import com.safeview.domain.administrator.dto.AdminRequestProcessDto;
import com.safeview.domain.administrator.dto.AdminRequestResponseDto;
import com.safeview.domain.administrator.dto.AdminRequestSummaryForAdminDto;
import com.safeview.domain.administrator.entity.AdminRequestStatus;

import java.util.List;

public interface AdminManagementService {

    // 모든 권한 요청 목록 조회 (관리자용) - 축약된 정보
    List<AdminRequestSummaryForAdminDto> getAllRequests();
    
    // 상태별 권한 요청 목록 조회 (관리자용) - 축약된 정보
    List<AdminRequestSummaryForAdminDto> getRequestsByStatus(AdminRequestStatus status);
    
    // 권한 요청 승인
    AdminRequestResponseDto approveRequest(Long requestId, Long adminId, String adminComment);
    
    // 권한 요청 거절
    AdminRequestResponseDto rejectRequest(Long requestId, Long adminId, String adminComment);
    
    // 대기중인 권한 요청만 조회 (관리자용) - 축약된 정보
    List<AdminRequestSummaryForAdminDto> getPendingRequests();
    
    // 권한 요청 상세 조회 (관리자용)
    AdminRequestResponseDto getRequestDetail(Long requestId);
} 