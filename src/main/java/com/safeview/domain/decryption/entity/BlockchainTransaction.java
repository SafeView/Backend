package com.safeview.domain.decryption.entity;

import com.safeview.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/*
 * 블록체인 트랜잭션 엔티티
 * 
 * 블록체인에서 발생한 트랜잭션 정보를 저장하는 엔티티
 * 키 발급, 취소 등의 블록체인 작업 이력을 관리
 */
@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "blockchain_transactions")
public class BlockchainTransaction extends BaseEntity {

    /*
     * 트랜잭션 ID (기본키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tx_id")
    private Long id;

    /*
     * 블록체인 트랜잭션 해시
     */
    @Column(name = "tx_hash", nullable = false, unique = true)
    private String txHash;

    /*
     * 블록 번호
     */
    @Column(name = "block_number")
    private Long blockNumber;

    /*
     * 송신자 주소
     */
    @Column(name = "from_address", nullable = false)
    private String fromAddress;

    /*
     * 수신자 주소
     */
    @Column(name = "to_address", nullable = false)
    private String toAddress;

    /*
     * 사용된 가스량
     */
    @Column(name = "gas_used")
    private Long gasUsed;

    /*
     * 가스 가격
     */
    @Column(name = "gas_price")
    private BigDecimal gasPrice;

    /*
     * 트랜잭션 상태 (PENDING, CONFIRMED, FAILED)
     */
    @Column(name = "status", nullable = false)
    private String status;

    /*
     * 트랜잭션 타입 (KEY_ISSUANCE, KEY_REVOCATION)
     */
    @Column(name = "tx_type", nullable = false)
    private String txType;

    /*
     * 트랜잭션 확정 시간
     */
    @Column(name = "confirmed_at")
    private java.time.LocalDateTime confirmedAt;

    /*
     * 에러 메시지
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
} 