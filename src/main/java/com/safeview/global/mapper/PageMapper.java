package com.safeview.global.mapper;

import com.safeview.global.response.CustomPageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


/**
 * 페이지네이션 매퍼
 * 
 * Spring Page 객체를 커스텀 페이지네이션 DTO로 변환하는 매퍼
 */
@Component
public class PageMapper {
    
    /**
     * Spring Page를 CustomPageResponseDto로 변환
     * 
     * @param page Spring Page 객체
     * @return CustomPageResponseDto
     */
    public static <T> CustomPageResponseDto<T> toCustomPageResponse(Page<T> page) {
        return CustomPageResponseDto.<T>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .content(page.getContent())
                .build();
    }
}
