package com.safeview.domain.user.controller;

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

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signup(@RequestBody SignUpRequest request) {
        SignUpResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }
}