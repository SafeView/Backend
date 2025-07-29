package com.safeview.domain.decryption.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

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