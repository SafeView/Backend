package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.LoginRequest;
import com.safeview.domain.user.dto.LoginResponse;
import com.safeview.domain.user.dto.SignUpRequest;
import com.safeview.domain.user.dto.SignUpResponse;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.repository.UserRepository;
import com.safeview.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * ✅ 회원가입 처리
     */
    public SignUpResponse register(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // 비번 암호화
                .nickname(request.getNickname())
                .role("ROLE_USER")
                .build();

        User saved = userRepository.save(newUser);

        return new SignUpResponse(saved.getId(), saved.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
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
        return new LoginResponse(token, user.getEmail(), user.getNickname());
    }
}