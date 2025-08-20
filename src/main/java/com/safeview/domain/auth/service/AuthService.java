package com.safeview.domain.auth.service;

import com.safeview.domain.auth.dto.UserInfoResponseDto;
import com.safeview.domain.auth.dto.UserLoginRequestDto;

/*
 * 인증 서비스 인터페이스
 * 
 * 사용자 로그인, 로그아웃, 사용자 정보 조회 기능을 제공
 */
public interface AuthService {
    UserInfoResponseDto.UserLoginResult login(UserLoginRequestDto request);
    UserInfoResponseDto getUserInfoById(Long userId);
    String logout();
}
