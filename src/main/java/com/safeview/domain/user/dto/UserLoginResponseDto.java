package com.safeview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponseDto {
    //private String token;
    private String email;
    private String name;
}