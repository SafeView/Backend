package com.safeview.domain.administrator.entity;

import com.safeview.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminRequest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;



    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminRequestStatus status;

    @Column(name = "admin_comment", columnDefinition = "TEXT")
    private String adminComment;

    @Column(name = "processed_at")
    private java.time.LocalDateTime processedAt;

    @Column(name = "processed_by")
    private Long processedBy;

    @Builder
    public AdminRequest(Long userId, String title, String description) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = AdminRequestStatus.PENDING;
    }

    public void approve(String adminComment, Long adminId) {
        this.status = AdminRequestStatus.APPROVED;
        this.adminComment = adminComment;
        this.processedAt = java.time.LocalDateTime.now();
        this.processedBy = adminId;
    }

    public void reject(String adminComment, Long adminId) {
        this.status = AdminRequestStatus.REJECTED;
        this.adminComment = adminComment;
        this.processedAt = java.time.LocalDateTime.now();
        this.processedBy = adminId;
    }
} 