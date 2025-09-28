package com.safeview.domain.auth.service;

import com.safeview.domain.auth.dto.UserLoginRequestDto;
import com.safeview.domain.auth.dto.UserLoginResult;

/*
 * 인증 서비스 인터페이스
 * 
 * 사용자 로그인, 로그아웃, 사용자 정보 조회 기능을 제공
 */
public interface AuthService {
    UserLoginResult login(UserLoginRequestDto request);
    String logout();
}
