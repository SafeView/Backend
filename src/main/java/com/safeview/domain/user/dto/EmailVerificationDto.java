package com.safeview.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 이메일 인증번호 검증 요청 DTO
 *
 * 이메일 인증번호 검증 시 클라이언트에서 전송하는 정보
 * 이메일 주소와 인증번호 포함
 */
@Getter
@Setter
public class EmailVerificationDto {

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 주소 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "인증번호는 필수 항목입니다.")
    @Pattern(regexp = "^[0-9]{6}$", message = "인증번호는 6자리 숫자여야 합니다.")
    private String code;
}
