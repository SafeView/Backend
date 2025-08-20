package com.safeview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * 이메일 중복 확인 응답 DTO
 * 
 * 이메일 중복 확인 결과를 클라이언트에게 반환하는 정보
 * 이메일 사용 가능 여부를 포함
 */
@Getter
@AllArgsConstructor
public class EmailCheckResponseDto {
    private final boolean available;
}
