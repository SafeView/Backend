package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.*;
import com.safeview.domain.user.entity.User;

public interface UserService {

    UserSignUpResponseDto signUp(UserSignUpRequestDto requestDto);
    EmailCheckResponseDto checkEmail(String email);
    UserLoginResult login(UserLoginRequestDto requestDto);

}