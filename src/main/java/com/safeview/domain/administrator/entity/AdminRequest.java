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

    /*
     * 권한을 요청한 사용자 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /*
     * 권한 요청 제목
     */
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    /*
     * 권한 요청 상세 설명
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /*
     * 권한 요청 상태
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminRequestStatus status;

    /*
     * 관리자 코멘트
     */
    @Column(name = "admin_comment", columnDefinition = "TEXT")
    private String adminComment;

    /*
     * 처리 시간
     */
    @Column(name = "processed_at")
    private java.time.LocalDateTime processedAt;

    /*
     * 처리한 관리자 ID
     */
    @Column(name = "processed_by")
    private Long processedBy;

    /*
     * 권한 요청 생성
     */
    @Builder
    public AdminRequest(Long userId, String title, String description) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = AdminRequestStatus.PENDING;  // 기본값: 대기중
    }

    /*
     * 권한 요청 승인 처리
     */
    public void approve(String adminComment, Long adminId) {
        this.status = AdminRequestStatus.APPROVED;
        this.adminComment = adminComment;
        this.processedAt = java.time.LocalDateTime.now();
        this.processedBy = adminId;
    }

    /*
     * 권한 요청 거절 처리
     */
    public void reject(String adminComment, Long adminId) {
        this.status = AdminRequestStatus.REJECTED;
        this.adminComment = adminComment;
        this.processedAt = java.time.LocalDateTime.now();
        this.processedBy = adminId;
    }
} 