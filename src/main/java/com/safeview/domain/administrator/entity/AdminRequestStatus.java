package com.safeview.domain.administrator.entity;

public enum AdminRequestStatus {
    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거절됨"),
    CANCELLED("취소됨");

    private final String description;

    AdminRequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 