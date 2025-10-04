package com.safeview.domain.auth.service;

import com.safeview.domain.auth.dto.UserLoginRequestDto;
import com.safeview.domain.auth.dto.UserLoginResponseDto;
import com.safeview.domain.auth.dto.UserLoginResult;
import com.safeview.domain.auth.mapper.AuthMapper;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.repository.UserRepository;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import com.safeview.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스 구현체
 * 
 * 사용자 인증 관련 비즈니스 로직을 담당합니다.
 * - 사용자 로그인 처리 (이메일/비밀번호 검증)
 * - JWT 토큰 생성 (Access Token, Refresh Token)
 * - 사용자 정보 조회
 * - 로그아웃 처리
 * 
 * 보안: 비밀번호 암호화 검증, JWT 토큰 관리
 * 감사: 로그인 시도 및 성공/실패 이력
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    /**
     * 사용자 로그인 처리
     * 
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @return 로그인 결과 (Access Token, Refresh Token, 사용자 정보)
     * 
     * 처리 과정:
     * 1. 이메일로 사용자 조회
     * 2. 비밀번호 암호화 검증
     * 3. JWT Access Token 생성
     * 4. JWT Refresh Token 생성
     * 5. 사용자 정보와 토큰 반환
     * 
     * 보안: 비밀번호 암호화 검증, JWT 토큰 생성
     * 예외: 존재하지 않는 이메일, 비밀번호 불일치
     */
    @Override
    @Transactional
    public UserLoginResult login(UserLoginRequestDto request) {
        log.info("사용자 로그인 시도: email={}", request.getEmail());
        
        // 이메일로 유저 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 이메일입니다."));

        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치: email={}", request.getEmail());
            throw new ApiException(ErrorCode.INVALID_PASSWORD, "비밀번호가 일치하지 않습니다.");
        }

        // Access Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole());

        // Refresh Token 생성
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        UserLoginResponseDto userInfo = authMapper.toLoginResponseDto(user);

        log.info("사용자 로그인 성공: userId={}, role={}", user.getId(), user.getRole());
        
        // 사용자 정보와 토큰들 함께 응답
        return new UserLoginResult(accessToken, refreshToken, userInfo);
    }


    /**
     * 사용자 로그아웃 처리
     * 
     * @return 로그아웃 완료 메시지
     * 
     * 처리 과정:
     * 1. 로그아웃 요청 로깅
     * 2. 클라이언트 측 토큰 무효화 (쿠키 삭제)
     * 3. 향후 확장: 토큰 블랙리스트 관리
     * 
     * 보안: 클라이언트 측 토큰 제거
     * 확장성: 서버 측 토큰 무효화 로직 추가 가능
     */
    @Override
    public String logout() {
        log.info("사용자 로그아웃 요청 처리");
        
        try {
            log.info("로그아웃 처리 완료");
            return "로그아웃이 완료되었습니다.";
            
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생", e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "로그아웃 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * Access Token 재발급
     * 
     * @param userId 사용자 ID
     * @return 새로운 Access Token
     * 
     * 처리 과정:
     * 1. 사용자 조회 및 역할 확인
     * 2. 새로운 Access Token 생성
     * 
     * 보안: 사용자 존재 여부 및 역할 확인
     * 예외: 존재하지 않는 사용자
     */
    @Override
    public String refreshAccessToken(Long userId) {
        log.info("Access Token 재발급 요청: userId={}", userId);
        
        try {
            // 사용자 조회 (역할 확인용)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

            // 새로운 Access Token 생성
            String newAccessToken = jwtTokenProvider.generateAccessToken(userId, user.getRole());
            
            log.info("Access Token 재발급 완료: userId={}, role={}", userId, user.getRole());
            return newAccessToken;
            
        } catch (ApiException e) {
            log.error("Access Token 재발급 실패: userId={}, error={}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Access Token 재발급 중 예상치 못한 오류: userId={}", userId, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "Access Token 재발급 중 오류가 발생했습니다.");
        }
    }
}
