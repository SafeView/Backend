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

/**
 * 관리자 권한 요청 관리 컨트롤러
 * 
 * ADMIN 권한을 가진 관리자가 사용자의 권한 요청을 관리하는 기능을 제공합니다.
 * - 모든 권한 요청 조회 (상태별, 대기중인 요청)
 * - 권한 요청 승인/거절
 * - 요청 상세 조회
 * 
 * 보안: @PreAuthorize("hasRole('ADMIN')")로 ADMIN 권한만 접근 가능
 */
@RestController
@RequestMapping("/api/admin/requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // 🔐 ADMIN 권한만 접근 가능
public class AdminManagementController {

    private final AdminManagementService adminManagementService;

    /**
     * 모든 요청 목록 조회 (관리자용) - 축약된 정보
     * 
     * @return 모든 사용자의 권한 요청 목록 (요약 정보)
     * 
     * 기능: 관리자가 모든 권한 요청을 한눈에 조회
     * 응답: 요약 정보만 포함하여 빠른 로딩
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminRequestSummaryForAdminDto>>> getAllRequests() {
        
        List<AdminRequestSummaryForAdminDto> requests = adminManagementService.getAllRequests();
        return ApiResponse.toResponseEntity(SuccessCode.OK, requests);
    }

    /**
     * 대기중인 요청 목록 조회 (관리자용) - 축약된 정보
     * 
     * @return PENDING 상태의 권한 요청 목록
     * 
     * 기능: 처리 대기중인 요청만 필터링하여 조회
     * 용도: 관리자가 우선적으로 처리할 요청 확인
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<AdminRequestSummaryForAdminDto>>> getPendingRequests() {
        
        List<AdminRequestSummaryForAdminDto> requests = adminManagementService.getPendingRequests();
        return ApiResponse.toResponseEntity(SuccessCode.OK, requests);
    }

    /**
     * 상태별 요청 목록 조회 (관리자용) - 축약된 정보
     * 
     * @param status 조회할 요청 상태 (PENDING, APPROVED, REJECTED)
     * @return 특정 상태의 권한 요청 목록
     * 
     * 기능: 상태별로 요청을 필터링하여 조회
     * 용도: 승인/거절 통계, 처리 이력 확인
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<AdminRequestSummaryForAdminDto>>> getRequestsByStatus(
            @PathVariable AdminRequestStatus status) {
        
        List<AdminRequestSummaryForAdminDto> requests = adminManagementService.getRequestsByStatus(status);
        return ApiResponse.toResponseEntity(SuccessCode.OK, requests);
    }

    /**
     * 요청 상세 조회 (관리자용)
     * 
     * @param requestId 조회할 권한 요청 ID
     * @return 특정 권한 요청의 상세 정보
     * 
     * 기능: 요청의 모든 상세 정보 조회
     * 포함: 사용자 정보, 요청 내용, 처리 상태, 관리자 코멘트 등
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<AdminRequestResponseDto>> getRequestDetail(@PathVariable Long requestId) {
        AdminRequestResponseDto request = adminManagementService.getRequestDetail(requestId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, request);
    }

    /**
     * 권한 요청 승인
     * 
     * @param requestId 승인할 권한 요청 ID
     * @param adminId 승인하는 관리자 ID (JWT 토큰에서 추출)
     * @param adminCommentDto 관리자 코멘트 정보
     * @return 승인 처리된 권한 요청 정보
     * 
     * 처리: 요청 상태를 APPROVED로 변경
     * 권한 변경: 사용자 역할을 USER → MODERATOR로 업그레이드
     * 기록: 승인한 관리자 ID와 코멘트 저장
     * 시간: 처리 시간 자동 기록
     */
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<ApiResponse<AdminRequestResponseDto>> approveRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Long adminId,  // 승인하는 관리자 ID
            @Valid @RequestBody AdminCommentDto adminCommentDto) {
        
        AdminRequestResponseDto response = adminManagementService.approveRequest(requestId, adminId, adminCommentDto.getAdminComment());
        return ApiResponse.toResponseEntity(SuccessCode.OK, response);
    }

    /**
     * 권한 요청 거절
     * 
     * @param requestId 거절할 권한 요청 ID
     * @param adminId 거절하는 관리자 ID (JWT 토큰에서 추출)
     * @param adminCommentDto 관리자 코멘트 정보
     * @return 거절 처리된 권한 요청 정보
     * 
     * 처리: 요청 상태를 REJECTED로 변경
     * 권한 유지: 사용자 역할 변경 없음 (USER 유지)
     * 기록: 거절한 관리자 ID와 코멘트 저장
     * 시간: 처리 시간 자동 기록
     */
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<ApiResponse<AdminRequestResponseDto>> rejectRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Long adminId,  // 거절하는 관리자 ID
            @Valid @RequestBody AdminCommentDto adminCommentDto) {
        
        AdminRequestResponseDto response = adminManagementService.rejectRequest(requestId, adminId, adminCommentDto.getAdminComment());
        return ApiResponse.toResponseEntity(SuccessCode.OK, response);
    }
}