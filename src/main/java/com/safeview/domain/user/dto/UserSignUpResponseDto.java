package com.safeview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 사용자 회원가입 응답 DTO
 * 
 * 회원가입 성공 시 클라이언트에게 반환하는 정보
 * 생성된 사용자 ID를 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpResponseDto {
    private Long id;
}