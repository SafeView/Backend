package com.safeview.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 커스텀 페이지네이션 응답 DTO
 * 
 * Spring의 기본 Page 객체 대신 간단하고 명확한 페이지네이션 정보를 제공
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomPageResponseDto<T> {
    
    /**
     * 현재 페이지 (0부터 시작)
     */
    private int page;
    
    /**
     * 페이지 크기
     */
    private int size;
    
    /**
     * 총 요소 수
     */
    private long totalElements;
    
    /**
     * 총 페이지 수
     */
    private int totalPages;
    
    /**
     * 첫 번째 페이지 여부
     */
    private boolean first;
    
    /**
     * 마지막 페이지 여부
     */
    private boolean last;
    
    /**
     * 데이터 목록
     */
    private List<T> content;
}
