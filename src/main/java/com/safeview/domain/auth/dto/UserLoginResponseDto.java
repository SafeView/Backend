package com.safeview.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 사용자 로그인 응답 DTO
 * 
 * 로그인 성공 시 사용자 정보를 반환하는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponseDto {
    private String email;
    private String name;
}
