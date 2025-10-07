package com.safeview.domain.dashboard.controller;

import com.safeview.domain.dashboard.dto.KeyStatsDto;
import com.safeview.domain.dashboard.dto.UserListResponseDto;
import com.safeview.domain.dashboard.dto.UserStatsDto;
import com.safeview.domain.dashboard.dto.YearlyKeyIssuanceDto;
import com.safeview.domain.dashboard.dto.YearlyNewUsersDto;
import com.safeview.domain.dashboard.service.DashboardService;
import com.safeview.domain.user.entity.Role;
import com.safeview.global.mapper.PageMapper;
import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.CustomPageResponseDto;
import com.safeview.global.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 대시보드 컨트롤러
 * 
 * 관리자 대시보드에서 필요한 통계 및 분석 데이터를 제공하는 API
 * - 사용자 통계 조회
 * - 월별 신규 가입자 수 조회
 * - 복호화 키 통계 조회
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    /**
     * 사용자 통계 조회
     * 
     * @param adminUserId 인증된 관리자 ID
     * @return 사용자 통계 정보 (총 사용자 수, 역할별 사용자 수)
     * 
     * 권한: ADMIN만 접근 가능
     * 기능: 대시보드에서 사용자 현황을 표시하기 위한 데이터 제공
     */
    @GetMapping("/user-stats")
    public ResponseEntity<ApiResponse<UserStatsDto>> getUserStats(
            @AuthenticationPrincipal Long adminUserId) {
        UserStatsDto userStats = dashboardService.getUserStats(adminUserId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, userStats);
    }
    
    /**
     * 1년간 월별 신규 가입자 수 조회
     * 
     * @param adminUserId 인증된 관리자 ID
     * @return 1년간 월별 신규 가입자 수 정보 (12개월 데이터)
     * 
     * 권한: ADMIN만 접근 가능
     * 기능: 대시보드에서 1년간 월별 신규 가입자 현황을 표시하기 위한 데이터 제공
     */
    @GetMapping("/yearly-new-users")
    public ResponseEntity<ApiResponse<YearlyNewUsersDto>> getYearlyNewUsers(
            @AuthenticationPrincipal Long adminUserId) {

                
        YearlyNewUsersDto yearlyNewUsers = dashboardService.getYearlyNewUsers(adminUserId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, yearlyNewUsers);
    }
    
    /**
     * 복호화 키 통계 조회
     * 
     * @param adminUserId 인증된 관리자 ID
     * @return 복호화 키 통계 정보 (총 발급 수, 상태별 수, 사용률 등)
     * 
     * 권한: ADMIN만 접근 가능
     * 기능: 대시보드에서 복호화 키 현황을 표시하기 위한 데이터 제공
     */
    @GetMapping("/key-stats")
    public ResponseEntity<ApiResponse<KeyStatsDto>> getKeyStats(
            @AuthenticationPrincipal Long adminUserId) {
        KeyStatsDto keyStats = dashboardService.getKeyStats(adminUserId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, keyStats);
    }
    
    /**
     * 1년간 월별 복호화 키 발급 조회
     * 
     * @param adminUserId 인증된 관리자 ID
     * @return 1년간 월별 복호화 키 발급 추이 정보 (12개월 데이터)
     * 
     * 권한: ADMIN만 접근 가능
     * 기능: 대시보드에서 1년간 월별 복호화 키 발급 추이를 표시하기 위한 데이터 제공
     */
    @GetMapping("/yearly-key-issuance")
    public ResponseEntity<ApiResponse<YearlyKeyIssuanceDto>> getYearlyKeyIssuance(
            @AuthenticationPrincipal Long adminUserId) {
        YearlyKeyIssuanceDto yearlyKeyIssuance = dashboardService.getYearlyKeyIssuance(adminUserId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, yearlyKeyIssuance);
    }
    
    /**
     * 회원 목록 조회 (관리자용)
     * 
     * @param adminUserId 인증된 관리자 ID
     * @param role 사용자 역할 (선택적, null이면 모든 역할)
     * @param pageable 페이징 정보 (page, size, sort)
     * @return 회원 목록 (페이지네이션)
     * 
     * 권한: ADMIN만 접근 가능
     * 기능: 관리자가 모든 회원 또는 특정 역할의 회원 목록을 조회
     * 
     * 사용 예시:
     * GET /api/dashboard/users?role=USER&page=0&size=10&sort=createdAt,desc
     * GET /api/dashboard/users?page=0&size=20&sort=name,asc
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<CustomPageResponseDto<UserListResponseDto>>> getUsers(
            @AuthenticationPrincipal Long adminUserId,
            @RequestParam(required = false) Role role,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<UserListResponseDto> users = dashboardService.getUsers(adminUserId, role, pageable);
        CustomPageResponseDto<UserListResponseDto> customPage = PageMapper.toCustomPageResponse(users);
        
        return ApiResponse.toResponseEntity(SuccessCode.OK, customPage);
    }
}
