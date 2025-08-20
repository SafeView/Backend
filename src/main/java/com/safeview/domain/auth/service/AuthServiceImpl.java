package com.safeview.domain.auth.service;

import com.safeview.domain.auth.dto.UserInfoResponseDto;
import com.safeview.domain.auth.dto.UserLoginRequestDto;
import com.safeview.domain.auth.dto.UserLoginResponseDto;
import com.safeview.domain.auth.mapper.AuthMapper;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.repository.UserRepository;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import com.safeview.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    @Override
    @Transactional
    public UserInfoResponseDto.UserLoginResult login(UserLoginRequestDto request) {
        // 이메일로 유저 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 이메일입니다."));

        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD, "비밀번호가 일치하지 않습니다.");
        }

        // Access Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole());

        // Refresh Token 생성
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        UserLoginResponseDto userInfo = authMapper.toLoginResponseDto(user);

        // 사용자 정보와 토큰들 함께 응답
        return new UserInfoResponseDto.UserLoginResult(accessToken, refreshToken, userInfo);
    }

    @Override
    public UserInfoResponseDto getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "해당 사용자가 존재하지 않습니다."));

        return authMapper.toUserInfoResponseDto(user);
    }

    @Override
    public String logout() {
        // 로그아웃 처리 (현재는 단순히 메시지 반환)
        // 향후 토큰 블랙리스트 관리나 세션 무효화 로직 추가 가능
        return "로그아웃이 완료되었습니다.";
    }
}
