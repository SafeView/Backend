package com.safeview.domain.dashboard.mapper;

import com.safeview.domain.dashboard.dto.KeyStatsDto;
import com.safeview.domain.dashboard.dto.UserListResponseDto;
import com.safeview.domain.dashboard.dto.UserStatsDto;
import com.safeview.domain.dashboard.dto.YearlyKeyIssuanceDto;
import com.safeview.domain.dashboard.dto.YearlyNewUsersDto;
import com.safeview.domain.user.entity.User;
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
    
    /**
     * 복호화 키 통계를 KeyStatsDto로 변환
     * 
     * @param totalKeys 총 발급된 키 수
     * @param activeKeys 활성 키 수
     * @param expiredKeys 만료된 키 수
     * @param revokedKeys 취소된 키 수
     * @param usageRate 키 사용률
     * @return KeyStatsDto 인스턴스
     */
    public KeyStatsDto toKeyStatsDto(Long totalKeys, Long activeKeys, Long expiredKeys, Long revokedKeys, 
                                   Double usageRate) {
        return KeyStatsDto.builder()
                .totalKeys(totalKeys)
                .activeKeys(activeKeys)
                .expiredKeys(expiredKeys)
                .revokedKeys(revokedKeys)
                .usageRate(usageRate)
                .build();
    }
    
    /**
     * 1년간 월별 복호화 키 발급 추이를 YearlyKeyIssuanceDto로 변환
     * 
     * @param currentYear 연도
     * @param monthlyDataList 월별 데이터 목록
     * @param totalIssuedKeys 총 발급된 키 수
     * @return YearlyKeyIssuanceDto 인스턴스
     */
    public YearlyKeyIssuanceDto toYearlyKeyIssuanceDto(int currentYear, List<YearlyKeyIssuanceDto.MonthlyData> monthlyDataList, long totalIssuedKeys) {
        return YearlyKeyIssuanceDto.builder()
                .year(currentYear)
                .monthlyData(monthlyDataList)
                .totalIssuedKeys(totalIssuedKeys)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * User 엔티티를 UserListResponseDto로 변환
     * 
     * @param user User 엔티티
     * @return UserListResponseDto 인스턴스
     */
    public UserListResponseDto toUserListResponseDto(User user) {
        return UserListResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .gender(user.getGender().name())
                .birthday(user.getBirthday())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
