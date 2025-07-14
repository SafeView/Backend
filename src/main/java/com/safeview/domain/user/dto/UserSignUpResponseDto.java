package com.safeview.domain.user.dto;

import com.safeview.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignUpResponseDto {
    private Long id;
    private String email;
    private String name;

    public static UserSignUpResponseDto from(User user) {
        return new UserSignUpResponseDto(user.getId(), user.getEmail(), user.getName());
    }
}