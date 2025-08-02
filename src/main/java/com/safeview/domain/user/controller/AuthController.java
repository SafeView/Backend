package com.safeview.domain.user.controller;

import com.safeview.domain.user.dto.*;
import com.safeview.domain.user.service.UserLoginResult;
import com.safeview.domain.user.service.UserService;
import com.safeview.global.response.ApiResponse;
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

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    //  로그인 성공 → 200 OK + 응답 바디 포함
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> login(
            @RequestBody UserLoginRequestDto request,
            HttpServletResponse response
    ) {
        UserLoginResult result = userService.login(request);
        String token = result.getToken();

        // 쿠키 생성 = JWT 토큰을 "accessToken"이라는 이름의 쿠키로 만들어 내려준다
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true) // HttpOnly 설정: JavaScript에서 쿠키 접근을 못하게 막을건지?
                .secure(false) // 개발환경, 배포 시 true
                .sameSite("Lax") // 쿠키의 CSRF 방지 정책, "Lax": 대부분의 요청엔 허용, POST 폼 제출도 허용됨 (기본 추천)
                .path("/") // path 설정, "/"로 설정하면 전체 사이트에서 쿠키 유효
                .maxAge(60 * 60) // 쿠키 유효 시간 (초 단위) : 1시간 유효
                .build();



        // 쿠키 헤더에 추가
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 응답에는 사용자 정보만 포함
        return ApiResponse.toResponseEntity(SuccessCode.OK, result.getUserInfo());
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getMyInfo(HttpServletRequest request) {
        // ✅ 쿠키에서 토큰 추출
        String token = jwtTokenProvider.resolveTokenFromCookie(request);

        // ✅ 토큰 유효성 검증
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(401).build(); // UNAUTHORIZED
        }

        // ✅ 토큰에서 userId 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // ✅ userId 기반으로 사용자 정보 조회
        UserInfoResponseDto userInfo = userService.getUserInfoById(userId);

        return ResponseEntity.ok(userInfo);
    }

}