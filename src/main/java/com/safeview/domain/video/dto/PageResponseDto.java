package com.safeview.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 페이지네이션 응답 DTO
 * 
 * 복잡한 Spring Page 객체를 간단한 형태로 변환
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    
    /**
     * 데이터 목록
     */
    private List<T> content;
    
    /**
     * 현재 페이지 번호 (0부터 시작)
     */
    private int page;
    
    /**
     * 페이지 크기
     */
    private int size;
    
    /**
     * 전체 데이터 개수
     */
    private long totalElements;
    
    /**
     * 전체 페이지 수
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
    
}
