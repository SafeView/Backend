package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.EmailCheckResponseDto;
import com.safeview.domain.user.dto.UserSignUpRequestDto;
import com.safeview.domain.user.dto.UserSignUpResponseDto;
import com.safeview.domain.user.dto.UserInfoResponseDto;
import com.safeview.domain.user.dto.UserUpdateRequestDto;
import com.safeview.domain.user.dto.TempPasswordRequestDto;
import com.safeview.domain.user.dto.TempPasswordResponseDto;
import com.safeview.domain.user.dto.EmailVerificationRequestDto;
import com.safeview.domain.user.dto.EmailVerificationDto;
import com.safeview.domain.user.dto.EmailVerificationResponseDto;

/**
 * 사용자 서비스 인터페이스
 * 
 * 사용자 관련 비즈니스 로직을 담당합니다.
 * - 회원가입 처리
 * - 이메일 중복 확인
 * - 사용자 정보 조회
 * - 사용자 정보 수정
 * - 임시 비밀번호 발송
 * - 이메일 인증번호 발송/검증
 * 
 * 보안: 비밀번호 암호화, 입력값 검증
 */
public interface UserService {
    UserSignUpResponseDto signUp(UserSignUpRequestDto requestDto);
    EmailCheckResponseDto checkEmail(String email);
    UserInfoResponseDto getUserInfoById(Long userId);
    UserInfoResponseDto updateUserInfo(Long userId, UserUpdateRequestDto requestDto);
    TempPasswordResponseDto sendTempPassword(TempPasswordRequestDto requestDto);
    EmailVerificationResponseDto sendEmailVerificationCode(EmailVerificationRequestDto requestDto);
    EmailVerificationResponseDto verifyEmailCode(EmailVerificationDto requestDto);
}