package com.safeview.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 1년간 월별 복호화 키 발급 추이 응답 DTO
 * 
 * 대시보드에서 1년간 월별 복호화 키 발급 현황을 표시하기 위한 데이터 전송 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YearlyKeyIssuanceDto {
    
    /**
     * 연도
     */
    private int year;
    
    /**
     * 월별 키 발급 데이터 목록
     */
    private List<MonthlyData> monthlyData;
    
    /**
     * 총 발급된 키 수 (1년간)
     */
    private long totalIssuedKeys;
    
    /**
     * 조회 시간
     */
    private LocalDateTime createdAt;
    
    /**
     * 월별 데이터 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyData {
        /**
         * 월 (1-12)
         */
        private int month;
        
        /**
         * 해당 월에 발급된 키 수
         */
        private Long issuedKeysCount;
    }
}
