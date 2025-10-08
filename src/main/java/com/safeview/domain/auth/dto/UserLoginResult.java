package com.safeview.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * 사용자 로그인 결과 DTO
 * 
 * 로그인 성공 시 토큰과 사용자 정보를 반환하는 DTO
 */
@Getter
@AllArgsConstructor
public class UserLoginResult {
    private String accessToken;
    private String refreshToken;
    private UserLoginResponseDto userInfo;
}