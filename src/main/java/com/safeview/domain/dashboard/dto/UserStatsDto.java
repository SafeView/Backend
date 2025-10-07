package com.safeview.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 통계 응답 DTO
 * 
 * 대시보드에서 사용자 현황을 표시하기 위한 데이터 전송 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDto {
    
    /**
     * 총 사용자 수
     */
    private Long totalUsers;
    
    /**
     * 일반 사용자 수
     */
    private Long userCount;
    
    /**
     * 중간관리자 수
     */
    private Long moderatorCount;
    
    /**
     * 관리자 수
     */
    private Long adminCount;
}
