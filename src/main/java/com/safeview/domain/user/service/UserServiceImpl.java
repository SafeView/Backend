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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /*
     * 회원가입
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

    /*
     * 이메일 중복 체크
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
