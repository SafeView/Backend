package com.safeview.domain.decryption.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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