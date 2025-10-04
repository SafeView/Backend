package com.safeview.domain.administrator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 관리자 권한 요청 처리 DTO
 * 
 * 관리자가 권한 요청을 승인하거나 거절할 때 사용하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class AdminRequestProcessDto {

    @NotNull(message = "처리 타입은 필수입니다")
    private ProcessType processType;

    private String adminComment;

    public enum ProcessType {
        APPROVE("승인"),
        REJECT("거절");

        private final String description;

        ProcessType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 