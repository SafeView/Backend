package com.safeview.domain.decryption.mapper;

import com.safeview.domain.decryption.dto.*;
import com.safeview.domain.decryption.entity.DecryptionKey;
import com.safeview.domain.decryption.entity.BlockchainTransaction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 복호화 키 관련 DTO 변환을 담당하는 매퍼
 * 빌더 패턴을 활용하여 안전하고 가독성 높은 객체 생성
 */
@Component
public class DecryptionKeyMapper {

    // ===== 키 발급 관련 =====

    /**
     * 키 발급 응답 DTO 변환
     * 빌더 패턴을 사용하여 필수 필드와 선택적 필드를 명확히 구분
     */
    public KeyIssuanceResponseDto toKeyIssuanceResponse(DecryptionKey decryptionKey, String accessToken) {
        return KeyIssuanceResponseDto.builder()
                // 🔐 보안 토큰 (필수)
                .accessToken(accessToken)
                
                // ⏰ 시간 정보 (필수)
                .expiresAt(decryptionKey.getExpiresAt())
                .issuedAt(decryptionKey.getIssuedAt())
                
                // 🔗 블록체인 정보
                .blockchainTxHash(decryptionKey.getBlockchainTxHash())
                .keyHash(decryptionKey.getKeyHash())
                
                // 📋 키 정보 (메타데이터)
                .keyType(decryptionKey.getKeyType())
                .keyStatus(decryptionKey.getStatus())
                .keyId(decryptionKey.getId())
                
                // 🔢 사용 횟수 정보
                .remainingUses(decryptionKey.getRemainingUses())
                .totalUses(decryptionKey.getRemainingUses() + (decryptionKey.getUsageHistory() != null ? decryptionKey.getUsageHistory().split(",").length : 0))
                .usedCount(decryptionKey.getUsageHistory() != null ? decryptionKey.getUsageHistory().split(",").length : 0)
                

                .build();
    }

    // ===== 키 목록 관련 =====

    /**
     * 키 목록 응답 DTO 변환
     * 페이지네이션 정보와 키 목록을 함께 빌드
     */
    public KeyListResponseDto toKeyListResponse(List<DecryptionKey> keys, int totalElements, int page, int size) {
        List<KeyListResponseDto.KeySummaryDto> keySummaries = keys.stream()
                .map(this::toKeySummaryDto)
                .collect(Collectors.toList());

        return KeyListResponseDto.builder()
                .keys(keySummaries)
                .totalCount(totalElements)
                .pageNumber(page)
                .pageSize(size)
                .build();
    }

    /**
     * 키 요약 DTO 변환
     * 목록에서 보여줄 핵심 정보만 포함
     */
    public KeyListResponseDto.KeySummaryDto toKeySummaryDto(DecryptionKey decryptionKey) {
        return KeyListResponseDto.KeySummaryDto.builder()
                .keyId(decryptionKey.getId())
                .userId(decryptionKey.getUserId())
                .keyType(decryptionKey.getKeyType())
                .status(decryptionKey.getStatus())
                .keyHash(decryptionKey.getKeyHash())
                .issuedAt(decryptionKey.getIssuedAt())
                .expiresAt(decryptionKey.getExpiresAt())
                .build();
    }

    // ===== 키 상세 관련 =====

    /**
     * 키 상세 응답 DTO 변환
     * 모든 상세 정보를 포함하여 빌드
     */
    public KeyDetailResponseDto toKeyDetailResponse(DecryptionKey decryptionKey) {
        return KeyDetailResponseDto.builder()
                // 📋 기본 정보
                .keyId(decryptionKey.getId())
                .userId(decryptionKey.getUserId())
                .keyType(decryptionKey.getKeyType())
                .status(decryptionKey.getStatus())
                .keyHash(decryptionKey.getKeyHash())
                
                // 🔗 블록체인 정보
                .blockchainTxHash(decryptionKey.getBlockchainTxHash())
                
                // ⏰ 시간 정보
                .issuedAt(decryptionKey.getIssuedAt())
                .expiresAt(decryptionKey.getExpiresAt())
                .revokedAt(decryptionKey.getRevokedAt())
                .createdAt(decryptionKey.getCreatedAt())
                .updatedAt(decryptionKey.getUpdatedAt())
                
                // 📝 취소 정보
                .revocationReason(decryptionKey.getRevocationReason())
                .build();
    }

    // ===== 키 검증 관련 =====

    /**
     * 키 검증 응답 DTO 변환 (기본)
     * 기본적인 검증 결과만 포함
     */
    public KeyVerificationResponseDto toKeyVerificationResponse(DecryptionKey decryptionKey, boolean isValid, String message) {
        return KeyVerificationResponseDto.builder()
                // ✅ 검증 결과 (필수)
                .isValid(isValid)
                .message(message)
                .canDecrypt(isValid)
                
                // ⏰ 시간 정보
                .expiresAt(decryptionKey.getExpiresAt())
                .build();
    }

    /**
     * 키 검증 응답 DTO 변환 (상세)
     * 복호화에 필요한 모든 정보를 포함하여 빌드
     */
    public KeyVerificationResponseDto toKeyVerificationResponse(DecryptionKey decryptionKey, boolean isValid, String message, 
                                                               String decryptionToken, String cameraId, 
                                                               boolean blockchainVerified) {
        return KeyVerificationResponseDto.builder()
                // ✅ 검증 결과 (필수)
                .isValid(isValid)
                .message(message)
                .canDecrypt(isValid)
                .verifiedAt(LocalDateTime.now())
                
                // ⏰ 시간 정보
                .expiresAt(decryptionKey.getExpiresAt())
                
                // 🔐 보안 정보
                .decryptionToken(decryptionToken)
                .remainingUses(decryptionKey.getRemainingUses())
                
                // 📹 CCTV 정보
                .cameraId(cameraId)
                
                // 🔗 블록체인 정보
                .blockchainTxHash(decryptionKey.getBlockchainTxHash())
                .blockchainVerified(blockchainVerified)
                .build();
    }

    // ===== 블록체인 트랜잭션 관련 =====

    /**
     * 블록체인 트랜잭션 응답 DTO 변환
     * 트랜잭션의 모든 상세 정보를 포함하여 빌드
     */
    public BlockchainTransactionResponseDto toBlockchainTransactionResponse(BlockchainTransaction transaction) {
        return BlockchainTransactionResponseDto.builder()
                // 📋 기본 정보
                .txId(transaction.getId())
                .txHash(transaction.getTxHash())
                .txType(transaction.getTxType())
                .status(transaction.getStatus())
                
                // 🔗 블록체인 정보
                .blockNumber(transaction.getBlockNumber())
                .fromAddress(transaction.getFromAddress())
                .toAddress(transaction.getToAddress())
                .gasUsed(transaction.getGasUsed())
                .gasPrice(transaction.getGasPrice())
                
                // ⏰ 시간 정보
                .confirmedAt(transaction.getConfirmedAt())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                
                // ❌ 오류 정보
                .errorMessage(transaction.getErrorMessage())
                .build();
    }

    // ===== 유틸리티 메서드 =====



    /**
     * 빈 키 목록 응답 생성
     * 빌더 패턴을 활용한 팩토리 메서드
     */
    public KeyListResponseDto createEmptyKeyListResponse(int page, int size) {
        return KeyListResponseDto.builder()
                .keys(List.of())
                .totalCount(0)
                .pageNumber(page)
                .pageSize(size)
                .build();
    }

    /**
     * 실패한 키 검증 응답 생성
     * 빌더 패턴을 활용한 팩토리 메서드
     */
    public KeyVerificationResponseDto createFailedVerificationResponse(String message) {
        return KeyVerificationResponseDto.builder()
                .isValid(false)
                .message(message)
                .canDecrypt(false)
                .verifiedAt(LocalDateTime.now())
                .build();
    }

    // ===== 엔티티 생성 메서드 =====

    /**
     * 새로운 복호화 키 엔티티 생성
     * 빌더 패턴을 활용한 엔티티 팩토리 메서드
     */
    public DecryptionKey createDecryptionKey(Long userId, String encryptedKey, String keyHash, 
                                           String blockchainTxHash, String accessToken,
                                           String keyType, LocalDateTime expiresAt, int defaultUses) {
        return DecryptionKey.builder()
                .userId(userId)
                .encryptedKey(encryptedKey)
                .keyHash(keyHash)
                .blockchainTxHash(blockchainTxHash)
                .keyType(keyType != null ? keyType : "CCTV_AES256")
                .expiresAt(expiresAt)
                .accessToken(accessToken)
                .remainingUses(defaultUses > 0 ? defaultUses : 90)
                .build();
    }

    /**
     * 업데이트된 복호화 키 엔티티 생성
     * 빌더 패턴을 활용한 엔티티 업데이트 메서드
     */
    public DecryptionKey createUpdatedDecryptionKey(DecryptionKey originalKey, String status, String revocationReason) {
        return originalKey.toBuilder()
                .status(status)
                .revokedAt(LocalDateTime.now())
                .revocationReason(revocationReason)
                .build();
    }

    /**
     * 토큰 무효화를 위한 업데이트된 복호화 키 엔티티 생성
     * 빌더 패턴을 활용한 토큰 무효화 메서드
     */


    /**
     * 블록체인 트랜잭션 엔티티 생성
     * 빌더 패턴을 활용한 엔티티 팩토리 메서드
     */
    public BlockchainTransaction createBlockchainTransaction(String txHash, String txType) {
        return BlockchainTransaction.builder()
                .txHash(txHash)
                .fromAddress("0xSystemAddress")
                .toAddress("0xContractAddress")
                .status("PENDING")
                .txType(txType)
                .build();
    }
} 