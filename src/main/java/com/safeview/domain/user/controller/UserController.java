package com.safeview.domain.user.controller;

import com.safeview.domain.user.dto.EmailCheckResponseDto;
import com.safeview.domain.user.dto.UserSignUpRequestDto;
import com.safeview.domain.user.dto.UserSignUpResponseDto;
import com.safeview.domain.user.service.UserService;
import com.safeview.global.resopnse.ApiResponse;
import com.safeview.global.resopnse.SuccessCode;
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
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignUpResponseDto>> signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) {
        UserSignUpResponseDto responseDto = userService.signUp(requestDto);
        return ApiResponse.toResponseEntity(SuccessCode.CREATED, responseDto);
    }

    @GetMapping("/check-email")
    public ApiResponse<EmailCheckResponseDto> checkEmailDuplicate(@RequestParam String email) {
        return ApiResponse.onSuccess(userService.checkEmail(email));
    }
}
