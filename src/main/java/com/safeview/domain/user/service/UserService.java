package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.*;

public interface UserService {

    UserSignUpResponseDto signUp(UserSignUpRequestDto requestDto);
    EmailCheckResponseDto checkEmail(String email);
    UserLoginResult login(UserLoginRequestDto requestDto);
    UserInfoResponseDto getUserInfoById(Long userId);
}