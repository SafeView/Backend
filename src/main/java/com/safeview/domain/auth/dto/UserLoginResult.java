package com.safeview.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResult {
    private String token;
    private UserLoginResponseDto userInfo;
}
