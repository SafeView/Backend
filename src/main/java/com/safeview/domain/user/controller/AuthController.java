package com.safeview.domain.user.controller;

import com.safeview.global.SuccessCode;
import com.safeview.global.ApiResponse;
import com.safeview.domain.user.dto.LoginRequest;
import com.safeview.domain.user.dto.LoginResponse;
import com.safeview.domain.user.dto.SignUpRequest;
import com.safeview.domain.user.dto.SignUpResponse;
import com.safeview.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    // ⭐ 회원가입 성공 → 201 Created + 응답 바디 포함
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signup(@RequestBody SignUpRequest request) {
        SignUpResponse response = userService.register(request);
        return ApiResponse.toResponseEntity(SuccessCode.CREATED, response);
    }

    // ⭐ 로그인 성공 → 200 OK + 응답 바디 포함
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ApiResponse.toResponseEntity(SuccessCode.OK, response);
    }
}