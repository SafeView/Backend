package com.safeview.domain.decryption.service;

import com.safeview.domain.decryption.dto.*;
import com.safeview.domain.decryption.entity.DecryptionKey;

/**
 * 복호화 서비스 인터페이스
 * 
 * CCTV 영상 복호화 관련 비즈니스 로직을 담당합니다.
 * - 복호화 키 발급/검증/취소
 * - 키 목록 조회 및 상세 정보 조회
 * - 블록체인 트랜잭션 조회
 * - 키 생성 및 암호화
 * 
 * 보안: 키 암호화, 토큰 검증, 블록체인 연동
 * 감사: 키 사용 이력, 블록체인 트랜잭션 추적
 */
public interface DecryptionService {

    // ===== 키 관리 =====
    
    /**
     * CCTV 복호화 키 발급
     */
    KeyIssuanceResponseDto issueKey(Long userId);

    /**
     * 키 목록 조회
     */
    KeyListResponseDto getKeyList(Long userId, int page, int size, String sortBy, String sortDir);

    /**
     * 키 해시로 조회
     */
    KeyDetailResponseDto getKeyByHash(String keyHash);

    /**
     * 키 검증 (사용자 ID 기반)
     */
    KeyVerificationResponseDto verifyKey(KeyVerificationRequestDto requestDto, Long userId);
    
    /**
     * 키 검증 (사용자 ID + 접근 토큰 + 카메라 ID로 검증)
     */
    KeyVerificationResponseDto verifyKeyByUserIdAndToken(KeyVerificationRequestDto requestDto, Long userId);

    /**
     * 키 검증 (접근 토큰 + 카메라 ID로 검증)
     */
    KeyVerificationResponseDto verifyKeyByToken(KeyVerificationRequestDto requestDto);

    /**
     * 키 취소
     */
    void revokeKey(KeyRevocationRequestDto requestDto, Long userId);

    /**
     * 블록체인 트랜잭션 조회
     */
    BlockchainTransactionResponseDto getTransaction(String txHash, Long userId);

    // ===== 키 생성 =====
    
    /**
     * CCTV 복호화용 AES-256 키 생성
     */
    String generateCCTVDecryptionKey();

    /**
     * 키 해시 생성 (SHA-256)
     */
    String generateKeyHash(String rawKey);

    /**
     * 키 암호화 (Base64 인코딩)
     */
    String encryptKey(String rawKey);

    /**
     * 보안 토큰 생성
     */
    String generateSecureToken();



    /**
     * 복호화용 임시 토큰 생성
     */
    String generateDecryptionToken();

    /**
     * 새로운 복호화 키 생성 및 저장
     */
    DecryptionKey createAndSaveDecryptionKey(Long userId, String encryptedKey, String keyHash, 
                                           String blockchainTxHash, String accessToken);

    // ===== 키 검증 =====
    
    /**
     * 검증 결과
     */
    class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        
        public static ValidationResult success() {
            return new ValidationResult(true, "검증 성공");
        }
        
        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }
    }

    /**
     * 키 검증 수행
     */
    ValidationResult validateKey(DecryptionKey decryptionKey, KeyVerificationRequestDto requestDto, Long userId);

    /**
     * 접근 토큰 검증
     */
    boolean verifyAccessToken(DecryptionKey decryptionKey, String accessToken);

    /**
     * 키 소유자 확인
     */
    boolean isKeyOwner(DecryptionKey decryptionKey, Long userId);

    /**
     * 키 활성 상태 확인
     */
    boolean isKeyActive(DecryptionKey decryptionKey);

    /**
     * 키 만료 확인
     */
    boolean isKeyExpired(DecryptionKey decryptionKey);

    /**
     * 사용 횟수 확인
     */
    boolean hasRemainingUses(DecryptionKey decryptionKey);



    /**
     * 키 취소 검증
     */
    void validateKeyRevocation(DecryptionKey decryptionKey, Long userId);

    /**
     * 키 사용 처리 (사용 횟수 감소)
     */
    void updateKeyUsage(DecryptionKey decryptionKey);

} 