package com.safeview.domain.auth.service;

import com.safeview.domain.auth.dto.UserLoginResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResult {
    private String token;
    private UserLoginResponseDto userInfo;
}
