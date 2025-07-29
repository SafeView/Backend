package com.safeview.domain.decryption.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyVerificationRequestDto {

    // 🔐 기본 인증 정보 (필수)
    @NotBlank(message = "접근 토큰은 필수입니다.")
    private String accessToken;  // 일회성 접근 토큰

    // 📹 CCTV 정보 (선택적)
    private String cameraId;     // CCTV 카메라 ID
    private String location;     // 위치 정보
    
    // 🎯 검증 목적 (선택적)
    private String purpose;      // 검증 목적 (예: "emergency", "investigation", "maintenance")
} 