package com.safeview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 임시 비밀번호 발송 응답 DTO
 *
 * 임시 비밀번호 발송 성공 시 사용하는 DTO
 */
@Getter
@AllArgsConstructor
public class TempPasswordResponseDto {
    private String message;
}
