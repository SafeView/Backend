package com.safeview.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 복호화 키 통계 응답 DTO
 * 
 * 대시보드에서 복호화 키 현황을 표시하기 위한 데이터 전송 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyStatsDto {
    
    /**
     * 총 발급된 키 수
     */
    private Long totalKeys;
    
    /**
     * 활성 키 수 (ACTIVE 상태)
     */
    private Long activeKeys;
    
    /**
     * 만료된 키 수 (EXPIRED 상태)
     */
    private Long expiredKeys;
    
    /**
     * 취소된 키 수 (REVOKED 상태)
     */
    private Long revokedKeys;
    
    /**
     * 키 사용률 (0.0 ~ 1.0)
     * 사용된 총 횟수 / 발급된 총 횟수
     */
    private Double usageRate;
    
}
