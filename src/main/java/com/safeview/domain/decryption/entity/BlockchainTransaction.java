package com.safeview.domain.decryption.entity;

import com.safeview.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "blockchain_transactions")
public class BlockchainTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tx_id")
    private Long id;

    @Column(name = "tx_hash", nullable = false, unique = true)
    private String txHash;

    @Column(name = "block_number")
    private Long blockNumber;

    @Column(name = "from_address", nullable = false)
    private String fromAddress;

    @Column(name = "to_address", nullable = false)
    private String toAddress;

    @Column(name = "gas_used")
    private Long gasUsed;

    @Column(name = "gas_price")
    private BigDecimal gasPrice;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, CONFIRMED, FAILED

    @Column(name = "tx_type", nullable = false)
    private String txType; // KEY_ISSUANCE, KEY_REVOCATION

    @Column(name = "confirmed_at")
    private java.time.LocalDateTime confirmedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
} 