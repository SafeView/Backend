package com.safeview.domain.dashboard.mapper;

import com.safeview.domain.dashboard.dto.UserStatsDto;
import com.safeview.domain.dashboard.dto.YearlyNewUsersDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 대시보드 매퍼
 * 
 * 대시보드 관련 DTO 변환 로직을 담당하는 매퍼
 */
@Component
public class DashboardMapper {
    
    /**
     * 사용자 통계를 UserStatsDto로 변환
     * 
     * @param totalUsers 총 사용자 수
     * @param userCount 일반 사용자 수
     * @param moderatorCount 중간관리자 수
     * @param adminCount 관리자 수
     * @return UserStatsDto 인스턴스
     */
    public UserStatsDto toUserStatsDto(Long totalUsers, Long userCount, Long moderatorCount, Long adminCount) {
        return UserStatsDto.builder()
                .totalUsers(totalUsers)
                .userCount(userCount)
                .moderatorCount(moderatorCount)
                .adminCount(adminCount)
                .build();
    }
    
    /**
     * 1년간 월별 신규 가입자 수를 YearlyNewUsersDto로 변환
     * 
     * @param currentYear 현재 연도
     * @param monthlyDataList 월별 데이터 리스트
     * @param totalNewUsers 총 신규 가입자 수
     * @return YearlyNewUsersDto 인스턴스
     */
    public YearlyNewUsersDto toYearlyNewUsersDto(int currentYear, List<YearlyNewUsersDto.MonthlyData> monthlyDataList, long totalNewUsers) {
        return YearlyNewUsersDto.builder()
                .year(currentYear)
                .monthlyData(monthlyDataList)
                .totalNewUsers(totalNewUsers)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
