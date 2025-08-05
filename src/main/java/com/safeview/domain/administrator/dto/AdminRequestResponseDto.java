package com.safeview.domain.administrator.dto;

import com.safeview.domain.administrator.entity.AdminRequestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminRequestResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private AdminRequestStatus status;
    private String adminComment;
    private LocalDateTime processedAt;
    private Long processedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 