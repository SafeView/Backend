package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.SignUpRequest;
import com.safeview.domain.user.dto.SignUpResponse;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
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
}