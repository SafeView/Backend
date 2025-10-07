package com.safeview.domain.dashboard.service;

import com.safeview.domain.dashboard.dto.KeyStatsDto;
import com.safeview.domain.dashboard.dto.UserStatsDto;
import com.safeview.domain.dashboard.dto.YearlyKeyIssuanceDto;
import com.safeview.domain.dashboard.dto.YearlyNewUsersDto;

/**
 * 대시보드 서비스 인터페이스
 * 
 * 관리자 대시보드에서 필요한 통계 및 분석 데이터를 제공하는 서비스
 */
public interface DashboardService {
    
    /**
     * 사용자 통계 조회
     * 
     * @param adminUserId 관리자 사용자 ID
     * @return 사용자 통계 정보 (총 사용자 수, 역할별 사용자 수)
     */
    UserStatsDto getUserStats(Long adminUserId);
    
    /**
     * 1년간 월별 신규 가입자 수 조회
     * 
     * @param adminUserId 관리자 사용자 ID
     * @return 1년간 월별 신규 가입자 수 정보 (12개월 데이터)
     */
    YearlyNewUsersDto getYearlyNewUsers(Long adminUserId);
    
    /**
     * 복호화 키 통계 조회
     * 
     * @param adminUserId 관리자 사용자 ID
     * @return 복호화 키 통계 정보 (총 발급 수, 상태별 수, 사용률 등)
     */
    KeyStatsDto getKeyStats(Long adminUserId);
    
    /**
     * 1년간 월별 복호화 키 발급 추이 조회
     * 
     * @param adminUserId 관리자 사용자 ID
     * @return 1년간 월별 복호화 키 발급 추이 정보 (12개월 데이터)
     */
    YearlyKeyIssuanceDto getYearlyKeyIssuance(Long adminUserId);
}
