package com.safeview.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 이메일 인증번호 발송 요청 DTO
 *
 * 이메일 인증번호 발송 시 클라이언트에서 전송하는 정보
 * 이메일 주소 포함
 */
@Getter
@Setter
public class EmailVerificationRequestDto {

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 주소 형식이 아닙니다.")
    private String email;
}
