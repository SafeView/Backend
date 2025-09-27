package com.safeview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class UserInfoResponseDto {
    private Long id;
    private String email;
    private String name;
    private String address;
    private String phone;
    private String gender;
    private String birthday;
    private String role;
    private String createdAt;
    private String updatedAt;
}