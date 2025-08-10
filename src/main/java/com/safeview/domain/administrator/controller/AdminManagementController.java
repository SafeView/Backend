package com.safeview.domain.administrator.controller;

import com.safeview.domain.administrator.dto.AdminCommentDto;
import com.safeview.domain.administrator.dto.AdminRequestResponseDto;
import com.safeview.domain.administrator.dto.AdminRequestSummaryForAdminDto;
import com.safeview.domain.administrator.entity.AdminRequestStatus;
import com.safeview.domain.administrator.service.AdminManagementService;
import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagementController {

    private final AdminManagementService adminManagementService;

    /**
     * 모든 요청 목록 조회 (관리자용) - 축약된 정보
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminRequestSummaryForAdminDto>>> getAllRequests() {
        
        List<AdminRequestSummaryForAdminDto> requests = adminManagementService.getAllRequests();
        return ApiResponse.toResponseEntity(SuccessCode.OK, requests);
    }

    /**
     * 대기중인 요청 목록 조회 (관리자용) - 축약된 정보
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<AdminRequestSummaryForAdminDto>>> getPendingRequests() {
        
        List<AdminRequestSummaryForAdminDto> requests = adminManagementService.getPendingRequests();
        return ApiResponse.toResponseEntity(SuccessCode.OK, requests);
    }

    /**
     * 상태별 요청 목록 조회 (관리자용) - 축약된 정보
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<AdminRequestSummaryForAdminDto>>> getRequestsByStatus(
            @PathVariable AdminRequestStatus status) {
        
        List<AdminRequestSummaryForAdminDto> requests = adminManagementService.getRequestsByStatus(status);
        return ApiResponse.toResponseEntity(SuccessCode.OK, requests);
    }

    /**
     * 요청 상세 조회 (관리자용)
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<AdminRequestResponseDto>> getRequestDetail(@PathVariable Long requestId) {
        AdminRequestResponseDto request = adminManagementService.getRequestDetail(requestId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, request);
    }


    /**
     * 권한 요청 승인
     */
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<ApiResponse<AdminRequestResponseDto>> approveRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Long adminId,
            @Valid @RequestBody AdminCommentDto adminCommentDto) {
        
        AdminRequestResponseDto response = adminManagementService.approveRequest(requestId, adminId, adminCommentDto.getAdminComment());
        return ApiResponse.toResponseEntity(SuccessCode.OK, response);
    }

    /**
     * 권한 요청 거절
     */
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<ApiResponse<AdminRequestResponseDto>> rejectRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Long adminId,
            @Valid @RequestBody AdminCommentDto adminCommentDto) {
        
        AdminRequestResponseDto response = adminManagementService.rejectRequest(requestId, adminId, adminCommentDto.getAdminComment());
        return ApiResponse.toResponseEntity(SuccessCode.OK, response);
    }
} 