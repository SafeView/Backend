package com.safeview.domain.decryption.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/*
 * 복호화 키 검증 요청 DTO
 * 
 * 복호화 키 검증을 요청할 때 사용하는 DTO
 * 접근 토큰과 CCTV 카메라 ID를 포함
 */
@Getter
@Setter
public class KeyVerificationRequestDto {

    // 🔐 키 접근 정보 (필수)
    @NotBlank(message = "접근 토큰은 필수입니다.")
    private String accessToken;  // 키 발급 시 받은 접근 토큰

    // 📹 CCTV 정보 (필수)
    @NotBlank(message = "카메라 ID는 필수입니다.")
    private String cameraId;     // CCTV 카메라 ID
} 