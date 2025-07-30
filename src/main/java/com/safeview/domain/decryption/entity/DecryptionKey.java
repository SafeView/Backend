package com.safeview.domain.decryption.entity;

import com.safeview.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "decryption_keys")
public class DecryptionKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "encrypted_key", nullable = false, columnDefinition = "TEXT")
    private String encryptedKey;

    @Column(name = "key_hash", nullable = false, unique = true)
    private String keyHash;

    @Column(name = "blockchain_tx_hash", nullable = false, unique = true)
    private String blockchainTxHash;

    @Column(name = "key_type", nullable = false)
    private String keyType; // CCTV 복호화용 AES-256 (고정)

    @Column(name = "status", nullable = false)
    private String status; // ACTIVE, EXPIRED, REVOKED

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revocation_reason")
    private String revocationReason;

    @Column(name = "access_token", unique = true)
    private String accessToken;  // 접근 토큰 (30일간 재사용 가능)

    @Column(name = "remaining_uses", nullable = false)
    private int remainingUses;  // 남은 사용 횟수

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;  // 마지막 사용 시간

    @Column(name = "usage_history", columnDefinition = "TEXT")
    private String usageHistory;  // 사용 이력 (JSON 형태)

    @PrePersist
    public void prePersist() {
        this.issuedAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    /**
     * 사용 횟수 감소
     */
    public void decrementRemainingUses() {
        if (this.remainingUses > 0) {
            this.remainingUses--;
            this.lastUsedAt = LocalDateTime.now();
        }
    }
} 