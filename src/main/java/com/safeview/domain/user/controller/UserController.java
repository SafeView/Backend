package com.safeview.domain.user.controller;

import com.safeview.domain.user.dto.EmailCheckResponseDto;
import com.safeview.domain.user.dto.UserSignUpRequestDto;
import com.safeview.domain.user.dto.UserSignUpResponseDto;
import com.safeview.domain.user.service.UserService;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.ErrorCode;
import com.safeview.global.response.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    // 회원가입 성공 → 201 Created + 응답 바디 포함
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignUpResponseDto>> signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) {
            UserSignUpResponseDto responseDto = userService.signUp(requestDto);
            return ApiResponse.toResponseEntity(SuccessCode.CREATED, responseDto);
        }

    @GetMapping("/check-email")
    public ApiResponse<EmailCheckResponseDto> checkEmail(@RequestParam String email) {
        if (email == null || !email.contains("@")) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "이메일 형식이 유효하지 않습니다.");
        }

        EmailCheckResponseDto response = userService.checkEmail(email);
        return ApiResponse.onSuccessWithMessage(response, "사용가능한 이메일입니다.");
    }
}
