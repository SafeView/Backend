package com.safeview.domain.video.mapper;

import com.safeview.domain.video.dto.PageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * 페이지네이션 매퍼
 * 
 * Spring Page 객체를 간단한 DTO로 변환하는 매퍼
 */
@Component
public class PageMapper {
    
    /**
     * Spring Page 객체를 PageResponseDto로 변환
     * 
     * @param page Spring Page 객체
     * @return PageResponseDto
     */
    public static <T> PageResponseDto<T> toPageResponse(Page<T> page) {
        return PageResponseDto.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
