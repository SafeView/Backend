package com.safeview.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/*
 * 임시 비밀번호 발송 요청 DTO
 * 
 * 이메일로 임시 비밀번호를 발송할 때 사용하는 DTO
 */
@Getter
@Setter
public class TempPasswordRequestDto {
    
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}
