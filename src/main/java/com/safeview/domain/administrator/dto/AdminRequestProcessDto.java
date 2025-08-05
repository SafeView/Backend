package com.safeview.domain.administrator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminRequestProcessDto {

    @NotNull(message = "처리 타입은 필수입니다")
    private ProcessType processType;

    @NotBlank(message = "관리자 코멘트는 필수입니다")
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