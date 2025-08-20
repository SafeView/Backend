package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.*;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.mapper.UserMapper;
import com.safeview.domain.user.repository.UserRepository;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스 구현체
 * 
 * 사용자 관련 비즈니스 로직을 담당합니다.
 * - 회원가입 처리 (이메일/전화번호 중복 확인, 비밀번호 암호화)
 * - 이메일 중복 확인
 * 
 * 보안: 비밀번호 암호화, 중복 데이터 검증
 * 감사: 회원가입 이력 관리
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * 회원가입 처리
     * 
     * @param requestDto 회원가입 요청 정보
     * @return 회원가입 성공 정보
     * 
     * 처리 과정:
     * 1. 이메일 중복 확인
     * 2. 전화번호 중복 확인
     * 3. User 엔티티 생성 (비밀번호 암호화 포함)
     * 4. 데이터베이스에 저장
     * 5. 회원가입 성공 응답 생성
     * 
     * 보안: 비밀번호 암호화, 중복 데이터 검증
     * 예외: 중복된 이메일, 중복된 전화번호
     */
    @Override
    @Transactional
    public UserSignUpResponseDto signUp(UserSignUpRequestDto requestDto) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 전화번호 중복 확인
        if (userRepository.existsByPhone(requestDto.getPhone())) {
            throw new ApiException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        // User 엔티티 생성
        User user = userMapper.toEntity(requestDto);

        User savedUser = userRepository.save(user);

        return userMapper.toSignUpResponseDto(savedUser);
    }

    /**
     * 이메일 중복 확인
     * 
     * @param email 확인할 이메일 주소
     * @return 이메일 사용 가능 여부
     * 
     * 처리 과정:
     * 1. 데이터베이스에서 이메일 존재 여부 확인
     * 2. 중복된 경우 예외 발생
     * 3. 사용 가능한 경우 true 반환
     * 
     * 예외: 중복된 이메일
     */
    @Override
    public EmailCheckResponseDto checkEmail(String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        
        if (exists) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS, new EmailCheckResponseDto(false));
        }
        
        return new EmailCheckResponseDto(true);
    }
}
