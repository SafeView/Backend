package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.*;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.repository.UserRepository;
import com.safeview.global.exception.ApiException;
import com.safeview.global.ErrorCode;
import com.safeview.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

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

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = requestDto.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);

        return UserSignUpResponseDto.from(savedUser);
    }

    /*
     * 이메일 중복 체크
     */
    @Override
    public EmailCheckResponseDto checkEmail(String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        String message = exists ? "이미 존재하는 이메일입니다." : "사용 가능한 이메일입니다.";
        if (exists) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS, new EmailCheckResponseDto(true, message));
        }
        return new EmailCheckResponseDto(false, message);
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto request) {
        // 이메일로 유저 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성
        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole());

        // 사용자 정보 함께 응답
        return new UserLoginResponseDto(token, user.getEmail(), user.getName());
    }
}
