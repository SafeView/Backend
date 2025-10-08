package com.safeview.domain.user.dto;

import com.safeview.domain.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 사용자 정보 응답 DTO
 * 
 * 사용자의 상세 정보를 반환하는 DTO
 */
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
