package com.safeview.domain.auth.service;

import com.safeview.domain.auth.dto.UserInfoResponseDto;
import com.safeview.domain.auth.dto.UserLoginRequestDto;

public interface AuthService {
    UserLoginResult login(UserLoginRequestDto request);
    UserInfoResponseDto getUserInfoById(Long userId);
    String logout();
}
