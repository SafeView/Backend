package com.safeview.domain.decryption.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * 블록체인 트랜잭션 응답 DTO
 * 
 * 블록체인 트랜잭션 정보를 조회할 때 사용하는 DTO
 * 트랜잭션 해시, 블록 정보, 가스 정보 등을 포함
 */
@Getter
@Builder
public class BlockchainTransactionResponseDto {

    private Long txId;
    private String txHash;
    private Long blockNumber;
    private String blockHash;
    private String fromAddress;
    private String toAddress;
    private Long gasUsed;
    private BigDecimal gasPrice;
    private String status;
    private String txType;
    private LocalDateTime confirmedAt;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 