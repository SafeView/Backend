package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.EmailCheckResponseDto;
import com.safeview.domain.user.dto.UserSignUpRequestDto;
import com.safeview.domain.user.dto.UserSignUpResponseDto;

public interface UserService {
    UserSignUpResponseDto signUp(UserSignUpRequestDto requestDto);
    EmailCheckResponseDto checkEmail(String email);
}