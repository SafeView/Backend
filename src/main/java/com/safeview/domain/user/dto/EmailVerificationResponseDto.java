package com.safeview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이메일 인증번호 응답 DTO
 *
 * 이메일 인증번호 발송/검증 성공 시 메시지 반환
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationResponseDto {
    private String message;
}
