package com.safeview.domain.auth.service;

import com.safeview.domain.auth.dto.UserLoginRequestDto;
import com.safeview.domain.auth.dto.UserLoginResultDto;

/*
 * 인증 서비스 인터페이스
 * 
 * 사용자 로그인, 로그아웃 기능을 제공
 */
public interface AuthService {
    UserLoginResultDto login(UserLoginRequestDto request);
    String logout();
}
