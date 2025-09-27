package com.safeview.domain.auth.mapper;

import com.safeview.domain.user.dto.UserInfoResponseDto;
import com.safeview.domain.auth.dto.UserLoginResponseDto;
import com.safeview.domain.user.entity.User;
import org.springframework.stereotype.Component;

/*
 * 인증 관련 매퍼
 * 
 * User 엔티티와 인증 관련 DTO 간의 변환을 담당
 */
@Component
public class AuthMapper {

    /*
     * User 엔티티를 UserLoginResponseDto로 변환
     */
    public UserLoginResponseDto toLoginResponseDto(User user) {
        return new UserLoginResponseDto(user.getEmail(), user.getName());
    }

    /*
     * User 엔티티를 UserInfoResponseDto로 변환
     */
    public UserInfoResponseDto toUserInfoResponseDto(User user) {
        return new UserInfoResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getAddress(),
                user.getPhone(),
                user.getGender().name(),
                user.getBirthday(),
                user.getRole().name(),
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString()
        );
    }
}
