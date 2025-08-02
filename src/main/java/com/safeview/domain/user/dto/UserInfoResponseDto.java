package com.safeview.domain.user.dto;

import com.safeview.domain.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * ✅ 프론트에 응답으로 전달할 사용자 정보 DTO
 */
@Getter
@Builder
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
