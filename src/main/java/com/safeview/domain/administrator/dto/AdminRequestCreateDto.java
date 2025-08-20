package com.safeview.domain.administrator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 관리자 권한 요청 생성 DTO
 * 
 * 사용자가 관리자 권한을 요청할 때 사용하는 DTO
 */
@Getter
@NoArgsConstructor
public class AdminRequestCreateDto {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다")
    private String title;

    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
    private String description;
} 