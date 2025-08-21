package com.safeview.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/*
 * 사용자 로그인 요청 DTO
 * 
 * 사용자가 로그인할 때 이메일과 비밀번호를 전송하는 DTO
 */
@Getter
@Setter
public class UserLoginRequestDto {
    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    private String password;
}
