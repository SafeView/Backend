package com.safeview.domain.user.controller;

import com.safeview.domain.user.dto.*;
import com.safeview.domain.user.service.UserService;
import com.safeview.global.ApiResponse;
import com.safeview.global.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    // ⭐ 회원가입 성공 → 201 Created + 응답 바디 포함
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignUpResponseDto>> signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) {
        UserSignUpResponseDto responseDto = userService.signUp(requestDto);
        return ApiResponse.toResponseEntity(SuccessCode.CREATED, responseDto);
    }

    // ⭐ 로그인 성공 → 200 OK + 응답 바디 포함
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> login(@RequestBody UserLoginRequestDto request) {
        UserLoginResponseDto response = userService.login(request);
        return ApiResponse.toResponseEntity(SuccessCode.OK, response);
    }


    @GetMapping("/check-email")
    public ApiResponse<EmailCheckResponseDto> checkEmailDuplicate(@RequestParam String email) {
        return ApiResponse.onSuccess(userService.checkEmail(email));
    }
}