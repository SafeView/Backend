package com.safeview.domain.decryption.entity;

import com.safeview.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/*
 * 복호화 키 엔티티
 * 
 * CCTV 영상 복호화를 위한 키 정보를 관리하는 엔티티
 * 블록체인과 연동하여 키의 무결성과 추적성을 보장
 */
@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "decryption_keys")
public class DecryptionKey extends BaseEntity {

    /*
     * 키 ID (기본키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private Long id;

    /*
     * 키를 발급받은 사용자 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /*
     * 암호화된 복호화 키
     */
    @Column(name = "encrypted_key", nullable = false, columnDefinition = "TEXT")
    private String encryptedKey;

    /*
     * 키 해시 (블록체인 검증용)
     */
    @Column(name = "key_hash", nullable = false, unique = true)
    private String keyHash;

    /*
     * 블록체인 트랜잭션 해시
     */
    @Column(name = "blockchain_tx_hash", nullable = false, unique = true)
    private String blockchainTxHash;

    /*
     * 키 타입 (CCTV 복호화용 AES-256)
     */
    @Column(name = "key_type", nullable = false)
    private String keyType;

    /*
     * 키 상태 (ACTIVE, EXPIRED, REVOKED)
     */
    @Column(name = "status", nullable = false)
    private String status;

    /*
     * 키 만료 시간
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /*
     * 키 발급 시간
     */
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    /*
     * 키 취소 시간
     */
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    /*
     * 키 취소 사유
     */
    @Column(name = "revocation_reason")
    private String revocationReason;

    /*
     * 접근 토큰 (30일간 재사용 가능)
     */
    @Column(name = "access_token", unique = true)
    private String accessToken;

    /*
     * 남은 사용 횟수
     */
    @Column(name = "remaining_uses", nullable = false)
    private int remainingUses;

    /*
     * 마지막 사용 시간
     */
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    /*
     * 사용 이력 (JSON 형태)
     */
    @Column(name = "usage_history", columnDefinition = "TEXT")
    private String usageHistory;

    /*
     * 엔티티 저장 전 실행되는 메서드
     * 
     * 발급 시간과 상태를 자동으로 설정
     */
    @PrePersist
    public void prePersist() {
        this.issuedAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    /*
     * 사용 횟수 감소
     * 
     * 키 사용 시 남은 사용 횟수를 1 감소시키고 마지막 사용 시간을 업데이트
     */
    public void decrementRemainingUses() {
        if (this.remainingUses > 0) {
            this.remainingUses--;
            this.lastUsedAt = LocalDateTime.now();
        }
    }
} 