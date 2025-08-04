package com.safeview.domain.administrator.dto;

import com.safeview.domain.administrator.entity.AdminRequestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminRequestSummaryDto {
    private Long id;
    private String title;
    private AdminRequestStatus status;
    private LocalDateTime createdAt;
} 