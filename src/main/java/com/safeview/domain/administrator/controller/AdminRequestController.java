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

/**
 * 관리자 권한 요청 컨트롤러
 * 
 * 사용자가 MODERATOR/ADMIN 권한을 요청하는 기능을 제공합니다.
 * - 권한 요청 생성
 * - 내 요청 목록/상세 조회
 * - 대기중인 요청 개수 조회
 */
@RestController
@RequestMapping("/api/admin-requests")
@RequiredArgsConstructor
public class AdminRequestController {

    private final AdminRequestService adminRequestService;

    /**
     * 권한 요청 생성 (MODERATOR/ADMIN 권한 요청)
     * 
     * @param userId JWT 토큰에서 추출된 현재 로그인한 사용자 ID
     * @param createDto 권한 요청 생성 정보 (제목, 설명)
     * @return 생성된 권한 요청 정보
     * 
     * 보안: @AuthenticationPrincipal로 인증된 사용자만 접근 가능
     * 검증: @Valid로 DTO 유효성 검증
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AdminRequestResponseDto>> createAdminRequest(
            @AuthenticationPrincipal Long userId,  
            @Valid @RequestBody AdminRequestCreateDto createDto) {

            // 입력 값 검증
            if (userId == null || userId <= 0) {
                throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
            }
            
            AdminRequestResponseDto response = adminRequestService.createAdminRequest(userId, createDto);
            return ApiResponse.toResponseEntity(SuccessCode.CREATED, response);
    }

    /**
     * 내 요청 목록 조회 (사용자용)
     * 
     * @param userId JWT 토큰에서 추출된 현재 로그인한 사용자 ID
     * @return 현재 사용자가 생성한 모든 권한 요청 목록 (요약 정보)
     * 
     * 기능: 사용자가 자신이 생성한 모든 권한 요청을 조회
     * 보안: 본인의 요청만 조회 가능
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminRequestSummaryDto>>> getMyRequests(
            @AuthenticationPrincipal Long userId) {

            // 입력 값 검증
            if (userId == null || userId <= 0) {
                throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
            }
            
            List<AdminRequestSummaryDto> requests = adminRequestService.getUserRequests(userId);
            return ApiResponse.toResponseEntity(SuccessCode.OK, requests);
    }

    /**
     * 내 요청 상세 조회 (사용자용)
     * 
     * @param userId JWT 토큰에서 추출된 현재 로그인한 사용자 ID
     * @param requestId 조회할 권한 요청 ID
     * @return 특정 권한 요청의 상세 정보
     * 
     * 보안: 본인의 요청만 조회 가능 (userId 검증)
     * 예외: 다른 사용자의 요청 조회 시 FORBIDDEN 에러
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<AdminRequestResponseDto>> getMyRequestDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long requestId) {

            // 입력 값 검증
            if (userId == null || userId <= 0) {
                throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
            }
            if (requestId == null || requestId <= 0) {
                throw new ApiException(ErrorCode.BAD_REQUEST, "유효하지 않은 요청 ID입니다.");
            }
            
            AdminRequestResponseDto request = adminRequestService.getAdminRequest(requestId);

            // 본인의 요청인지 확인
            if (!request.getUserId().equals(userId)) {
                throw new ApiException(ErrorCode.FORBIDDEN, "본인의 요청만 조회할 수 있습니다.");
            }

            return ApiResponse.toResponseEntity(SuccessCode.OK, request);
    }

    /**
     * 내 대기중인 요청 개수 조회 (사용자용)
     * 
     * @param userId JWT 토큰에서 추출된 현재 로그인한 사용자 ID
     * @return 현재 사용자의 대기중인 권한 요청 개수
     * 
     * 기능: 대시보드나 알림에서 사용할 대기중인 요청 개수 제공
     * 성능: 개수만 조회하므로 빠른 응답
     */
    @GetMapping("/pending/count")
    public ResponseEntity<ApiResponse<Long>> getMyPendingRequestCount(@AuthenticationPrincipal Long userId) {

            // 입력 값 검증
            if (userId == null || userId <= 0) {
                throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
            }
            
            long count = adminRequestService.getPendingRequestCountByUserId(userId);
            return ApiResponse.toResponseEntity(SuccessCode.OK, count);
    }

} 