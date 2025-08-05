package com.safeview.domain.administrator.controller;

import com.safeview.domain.administrator.dto.AdminRequestCreateDto;
import com.safeview.domain.administrator.dto.AdminRequestResponseDto;
import com.safeview.domain.administrator.dto.AdminRequestSummaryDto;
import com.safeview.domain.administrator.service.AdminRequestService;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.ErrorCode;
import com.safeview.global.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin-requests")
@RequiredArgsConstructor
public class AdminRequestController {

    private final AdminRequestService adminRequestService;

    /**
     * 권한 요청 생성 (MODERATOR/ADMIN 권한 요청)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AdminRequestResponseDto>> createAdminRequest(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AdminRequestCreateDto createDto) {
        
        AdminRequestResponseDto response = adminRequestService.createAdminRequest(userId, createDto);
        return ApiResponse.toResponseEntity(SuccessCode.CREATED, response);
    }

    /**
     * 내 요청 목록 조회 (사용자용)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminRequestSummaryDto>>> getMyRequests(
            @AuthenticationPrincipal Long userId) {
        
        List<AdminRequestSummaryDto> requests = adminRequestService.getUserRequests(userId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, requests);
    }

    /**
     * 내 요청 상세 조회 (사용자용)
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<AdminRequestResponseDto>> getMyRequestDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long requestId) {
        
        AdminRequestResponseDto request = adminRequestService.getAdminRequest(requestId);
        
        // 본인의 요청인지 확인
        if (!request.getUserId().equals(userId)) {
            throw new ApiException(ErrorCode.FORBIDDEN, "본인의 요청만 조회할 수 있습니다.");
        }
        
        return ApiResponse.toResponseEntity(SuccessCode.OK, request);
    }

    /**
     * 내 대기중인 요청 개수 조회 (사용자용)
     */
    @GetMapping("/pending/count")
    public ResponseEntity<ApiResponse<Long>> getMyPendingRequestCount(@AuthenticationPrincipal Long userId) {
        long count = adminRequestService.getPendingRequestCountByUserId(userId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, count);
    }

} 