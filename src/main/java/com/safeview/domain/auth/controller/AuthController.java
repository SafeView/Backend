package com.safeview.domain.auth.controller;

import com.safeview.domain.auth.dto.UserLoginRequestDto;
import com.safeview.domain.auth.dto.UserLoginResponseDto;
import com.safeview.domain.auth.dto.UserInfoResponseDto;
import com.safeview.domain.auth.service.AuthService;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.ErrorCode;
import com.safeview.global.response.SuccessCode;
import com.safeview.global.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    //  로그인 성공 → 200 OK + 응답 바디 포함
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> login(
            @RequestBody UserLoginRequestDto request,
            HttpServletResponse response
    ) {
        UserInfoResponseDto.UserLoginResult result = authService.login(request);
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

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
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

    // 토큰 재발급 (Refresh Token 사용)
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
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

        // 사용자 정보 조회
        UserInfoResponseDto userInfo = authService.getUserInfoById(userId);

        // 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, 
                com.safeview.domain.user.entity.Role.valueOf(userInfo.getRole()));

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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getMyInfo(HttpServletRequest request) {
        // ✅ 쿠키에서 토큰 추출
        String token = jwtTokenProvider.resolveTokenFromCookie(request);

        // ✅ 토큰 존재 여부 검증
        if (token == null) {
            throw new ApiException(ErrorCode.MISSING_JWT_TOKEN);
        }

        // ✅ 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ApiException(ErrorCode.INVALID_JWT_TOKEN);
        }

        // ✅ Access Token인지 확인
        if (!jwtTokenProvider.isAccessToken(token)) {
            throw new ApiException(ErrorCode.INVALID_JWT_TOKEN, "Access Token이 아닙니다.");
        }

        // ✅ 토큰에서 userId 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // ✅ userId 기반으로 사용자 정보 조회
        UserInfoResponseDto userInfo = authService.getUserInfoById(userId);

        return ApiResponse.toResponseEntity(SuccessCode.OK, userInfo);
    }
}
