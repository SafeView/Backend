package com.safeview.domain.decryption.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyRevocationRequestDto {

    @NotBlank(message = "액세스 토큰은 필수입니다.")
    private String accessToken;

    @NotBlank(message = "취소 사유는 필수입니다.")
    private String revocationReason;
} 