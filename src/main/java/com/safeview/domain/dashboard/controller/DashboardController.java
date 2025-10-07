package com.safeview.domain.dashboard.controller;

import com.safeview.domain.dashboard.dto.UserStatsDto;
import com.safeview.domain.dashboard.dto.YearlyNewUsersDto;
import com.safeview.domain.dashboard.service.DashboardService;
import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 대시보드 컨트롤러
 * 
 * 관리자 대시보드에서 필요한 통계 및 분석 데이터를 제공하는 API
 * - 사용자 통계 조회
 * - 월별 신규 가입자 수 조회
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
}
