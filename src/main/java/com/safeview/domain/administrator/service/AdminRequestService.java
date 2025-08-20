package com.safeview.domain.administrator.service;

import com.safeview.domain.administrator.dto.AdminRequestCreateDto;
import com.safeview.domain.administrator.dto.AdminRequestResponseDto;
import com.safeview.domain.administrator.dto.AdminRequestSummaryDto;
import java.util.List;

public interface AdminRequestService {

    // 권한 요청 생성 (MODERATOR/ADMIN 권한 요청)
    AdminRequestResponseDto createAdminRequest(Long userId, AdminRequestCreateDto createDto);

    // 사용자별 요청 조회 (축약된 정보)
    List<AdminRequestSummaryDto> getUserRequests(Long userId);

    // 요청 상세 조회
    AdminRequestResponseDto getAdminRequest(Long requestId);

    // 사용자별 대기중인 요청 개수 조회
    long getPendingRequestCountByUserId(Long userId);
} 