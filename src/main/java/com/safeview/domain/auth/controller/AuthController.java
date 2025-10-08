package com.safeview.domain.auth.controller;

import com.safeview.domain.auth.dto.UserLoginRequestDto;
import com.safeview.domain.auth.dto.UserLoginResponseDto;
import com.safeview.domain.auth.dto.UserLoginResult;
import com.safeview.domain.auth.service.AuthService;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.ErrorCode;
import com.safeview.global.response.SuccessCode;
import com.safeview.global.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러
 * 
 * 사용자 인증 관련 API를 제공합니다.
 * - 로그인/로그아웃 처리
 * - JWT 토큰 관리 (Access Token, Refresh Token)
 * - 토큰 재발급
 * 
 * 보안: JWT 토큰 기반 인증, HttpOnly 쿠키 사용
 * 쿠키: Access Token (1시간), Refresh Token (7일)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 사용자 로그인
     * 
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @param response HTTP 응답 객체 (쿠키 설정용)
     * @return 로그인 성공 시 사용자 정보
     * 
     * 처리 과정:
     * 1. 이메일/비밀번호 검증
     * 2. JWT 토큰 생성 (Access Token, Refresh Token)
     * 3. HttpOnly 쿠키에 토큰 저장
     * 4. 사용자 정보 반환
     * 
     * 쿠키 설정:
     * - Access Token: 1시간 유효, HttpOnly
     * - Refresh Token: 7일 유효, HttpOnly
     * 
     * 보안: HttpOnly 쿠키로 XSS 공격 방지
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> login(
            @Valid @RequestBody UserLoginRequestDto request,
            HttpServletResponse response
    ) {
        log.info("사용자 로그인 요청: email={}", request.getEmail());
        
        UserLoginResult result = authService.login(request);
        String accessToken = result.getAccessToken();
        String refreshToken = result.getRefreshToken();

        // Access Token 쿠키 생성
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false) // 개발환경, 배포 시 true
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60) // 1시간
                .build();

        // Refresh Token 쿠키 생성
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // 개발환경, 배포 시 true
                .sameSite("Lax")
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7일
                .build();

        // 쿠키 헤더에 추가
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // 응답에는 사용자 정보만 포함
        return ApiResponse.toResponseEntity(SuccessCode.OK, result.getUserInfo());
    }

    /**
     * 사용자 로그아웃
     * 
     * @param response HTTP 응답 객체 (쿠키 삭제용)
     * @return 로그아웃 성공 메시지
     * 
     * 처리 과정:
     * 1. 서비스에서 로그아웃 처리
     * 2. Access Token 쿠키 삭제
     * 3. Refresh Token 쿠키 삭제
     * 
     * 보안: 모든 인증 토큰 제거로 세션 종료
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        log.info("사용자 로그아웃 요청");
        
        // 서비스에서 로그아웃 처리
        String logoutMessage = authService.logout();
        
        // Access Token 쿠키 삭제
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        // Refresh Token 쿠키 삭제
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        // 쿠키 헤더에 추가
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ApiResponse.toResponseEntity(SuccessCode.OK, logoutMessage);
    }

    /**
     * JWT 토큰 재발급
     * 
     * @param request HTTP 요청 객체 (Refresh Token 추출용)
     * @param response HTTP 응답 객체 (새 Access Token 쿠키 설정용)
     * @return 토큰 재발급 성공 메시지
     * 
     * 처리 과정:
     * 1. 쿠키에서 Refresh Token 추출
     * 2. Refresh Token 유효성 검증
     * 3. 사용자 정보 조회
     * 4. 새로운 Access Token 생성
     * 5. HttpOnly 쿠키에 새 토큰 저장
     * 
     * 보안: Refresh Token 검증 후 Access Token 재발급
     * 예외: 유효하지 않은 Refresh Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.info("Access Token 재발급 요청");
        
        // Refresh Token 추출
        String refreshToken = jwtTokenProvider.resolveRefreshTokenFromCookie(request);

        // Refresh Token 존재 여부 검증
        if (refreshToken == null) {
            throw new ApiException(ErrorCode.MISSING_JWT_TOKEN, "Refresh Token이 없습니다.");
        }

        // Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ApiException(ErrorCode.INVALID_JWT_TOKEN, "유효하지 않은 Refresh Token입니다.");
        }

        // Refresh Token인지 확인
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new ApiException(ErrorCode.INVALID_JWT_TOKEN, "Refresh Token이 아닙니다.");
        }

        // Refresh Token에서 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 새로운 Access Token 생성 (사용자 정보 조회)
        String newAccessToken = authService.refreshAccessToken(userId);

        // 새로운 Access Token 쿠키 생성
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60) // 1시간
                .build();

        // 쿠키 헤더에 추가
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        return ApiResponse.toResponseEntity(SuccessCode.OK, "Access Token이 재발급되었습니다.");
    }

}
