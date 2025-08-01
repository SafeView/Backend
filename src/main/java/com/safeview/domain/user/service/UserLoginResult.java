package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.UserLoginResponseDto;
import lombok.Getter;

@Getter
public class UserLoginResult {
    private final String token;
    private final UserLoginResponseDto userInfo;

    public UserLoginResult(String token, UserLoginResponseDto userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }

}
