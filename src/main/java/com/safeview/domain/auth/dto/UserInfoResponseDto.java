package com.safeview.domain.auth.dto;

import com.safeview.domain.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {
    private Long id;
    private String email;
    private String name;
    private String address;
    private String phone;
    private Gender gender;
    private String birthday;
    private String role;
    private String createdAt;
    private String updatedAt;
}
