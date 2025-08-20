package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.EmailCheckResponseDto;
import com.safeview.domain.user.dto.UserSignUpRequestDto;
import com.safeview.domain.user.dto.UserSignUpResponseDto;

/**
 * 사용자 서비스 인터페이스
 * 
 * 사용자 관련 비즈니스 로직을 담당합니다.
 * - 회원가입 처리
 * - 이메일 중복 확인
 * 
 * 보안: 비밀번호 암호화, 입력값 검증
 */
public interface UserService {
    UserSignUpResponseDto signUp(UserSignUpRequestDto requestDto);
    EmailCheckResponseDto checkEmail(String email);
}