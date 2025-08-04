package com.safeview.domain.decryption.service;

import com.safeview.domain.decryption.dto.*;
import com.safeview.domain.decryption.entity.DecryptionKey;
import com.safeview.domain.decryption.entity.BlockchainTransaction;
import com.safeview.domain.decryption.repository.DecryptionKeyRepository;
import com.safeview.domain.decryption.repository.BlockchainTransactionRepository;
import com.safeview.domain.decryption.config.DecryptionConfig;
import com.safeview.domain.decryption.mapper.DecryptionKeyMapper;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DecryptionServiceImpl implements DecryptionService {

    // ===== Dependencies =====
    private final DecryptionKeyRepository decryptionKeyRepository;
    private final BlockchainTransactionRepository blockchainTransactionRepository;
    private final DecryptionConfig decryptionConfig;
    private final DecryptionKeyMapper decryptionKeyMapper;
    private final BlockchainService blockchainService;

    // ===== 키 관리 메서드 =====

    @Override
    @Transactional
    public KeyIssuanceResponseDto issueKey(Long userId) {
        log.info("CCTV 복호화 키 발급 요청: userId={}", userId);

        // 기존 유효한 키가 있는지 확인
        Optional<DecryptionKey> existingKey = findValidKeyByUserId(userId);
        
        if (existingKey.isPresent()) {
            DecryptionKey validKey = existingKey.get();
            log.info("기존 유효한 키 반환: keyId={}, remainingUses={}", validKey.getId(), validKey.getRemainingUses());
            return decryptionKeyMapper.toKeyIssuanceResponse(validKey, validKey.getAccessToken());
        }

        // 새로운 키 생성
        log.info("새로운 키 생성: userId={}", userId);
        String rawKey = generateCCTVDecryptionKey();
        String keyHash = generateKeyHash(rawKey);
        String encryptedKey = encryptKey(rawKey);
        
        // 보안 토큰 생성
        String accessToken = generateSecureToken();

        // 블록체인에 등록
        String blockchainTxHash = registerKeyOnBlockchain(keyHash, userId);

        // 키 저장
        DecryptionKey savedKey = createAndSaveDecryptionKey(
                userId, encryptedKey, keyHash, blockchainTxHash, accessToken);

        log.info("새로운 키 발급 완료: keyId={}, blockchainTxHash={}", savedKey.getId(), blockchainTxHash);
        return decryptionKeyMapper.toKeyIssuanceResponse(savedKey, accessToken);
    }

    @Override
    public KeyListResponseDto getKeyList(Long userId, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        Page<DecryptionKey> keyPage = decryptionKeyRepository.findByUserId(userId, pageable);
        
        // 빈 목록인 경우 빈 응답 생성
        if (keyPage.getContent().isEmpty()) {
            return decryptionKeyMapper.createEmptyKeyListResponse(page, size);
        }
        
        return decryptionKeyMapper.toKeyListResponse(
                keyPage.getContent(),
                (int) keyPage.getTotalElements(),
                keyPage.getNumber(),
                keyPage.getSize()
        );
    }

    @Override
    public KeyDetailResponseDto getKeyByHash(String keyHash) {
        DecryptionKey decryptionKey = findKeyByHash(keyHash);
        return decryptionKeyMapper.toKeyDetailResponse(decryptionKey);
    }

    @Override
    @Transactional
    public KeyVerificationResponseDto verifyKey(KeyVerificationRequestDto requestDto, Long userId) {
        log.info("키 검증 요청: userId={}, accessToken={}", userId, requestDto.getAccessToken());
        
        // 키 검증
        DecryptionKey decryptionKey = findKeyByAccessToken(requestDto.getAccessToken());
        ValidationResult validationResult = validateKey(decryptionKey, requestDto, userId);
        
        if (!validationResult.isValid()) {
            log.warn("키 검증 실패: {}", validationResult.getMessage());
            return decryptionKeyMapper.createFailedVerificationResponse(validationResult.getMessage());
        }
        
        // 블록체인 유효성 확인
        boolean blockchainValid = isKeyValidOnBlockchain(decryptionKey);
        
        // 키 사용 처리 (토큰 무효화하지 않고 사용 횟수만 감소)
        updateKeyUsage(decryptionKey);
        
        // 복호화 토큰 생성
        String decryptionToken = generateDecryptionToken();
        
        log.info("키 검증 성공: keyId={}, remainingUses={}", decryptionKey.getId(), decryptionKey.getRemainingUses());
        
        return decryptionKeyMapper.toKeyVerificationResponse(
                decryptionKey, true, "키 검증 성공", decryptionToken, 
                requestDto.getCameraId(), blockchainValid);
    }

    @Override
    @Transactional
    public KeyVerificationResponseDto verifyKeyByUserIdAndToken(KeyVerificationRequestDto requestDto, Long userId) {
        log.info("사용자 ID+접근 토큰+카메라 ID 키 검증 요청: userId={}, accessToken={}, cameraId={}", 
                userId, requestDto.getAccessToken(), requestDto.getCameraId());
        
        // 1. 접근 토큰으로 키 조회
        DecryptionKey decryptionKey = findKeyByAccessToken(requestDto.getAccessToken());
        
        // 2. 키 기본 유효성 검증 (사용자 ID 포함)
        ValidationResult validationResult = validateKeyByUserIdAndToken(decryptionKey, requestDto, userId);
        
        if (!validationResult.isValid()) {
            log.warn("키 검증 실패: {}", validationResult.getMessage());
            return decryptionKeyMapper.createFailedVerificationResponse(validationResult.getMessage());
        }
        
        // 3. 블록체인 유효성 확인
        boolean blockchainValid = isKeyValidOnBlockchain(decryptionKey);
        
        // 4. 키 사용 처리 (사용 횟수 감소)
        updateKeyUsage(decryptionKey);
        
        // 5. 복호화 토큰 생성
        String decryptionToken = generateDecryptionToken();
        
        log.info("키 검증 성공: keyId={}, userId={}, cameraId={}, remainingUses={}", 
                decryptionKey.getId(), userId, requestDto.getCameraId(), decryptionKey.getRemainingUses());
        
        return decryptionKeyMapper.toKeyVerificationResponse(
                decryptionKey, true, "키 검증 성공", decryptionToken, 
                requestDto.getCameraId(), blockchainValid);
    }

    @Override
    @Transactional
    public KeyVerificationResponseDto verifyKeyByToken(KeyVerificationRequestDto requestDto) {
        log.info("사용자 ID+접근 토큰+카메라 ID 키 검증 요청: accessToken={}, cameraId={}",
                requestDto.getAccessToken(), requestDto.getCameraId());

        // 1. 접근 토큰으로키 조회
        DecryptionKey decryptionKey = findKeyByAccessToken(requestDto.getAccessToken());

        // 2. 키 기본 유효성 검증 (사용자 ID 포함)
        ValidationResult validationResult = validateKeyByToken(decryptionKey, requestDto);

        if(!validationResult.isValid()){
            log.warn("키 검증 실패: {}", validationResult.getMessage());
            return decryptionKeyMapper.createFailedVerificationResponse(validationResult.getMessage());
        }

        // 3. 블록체인 유효성 확인
        boolean blockchainValid = isKeyValidOnBlockchain(decryptionKey);

        // 4. 키 사용 처리 (사용 횟수 감소)
        updateKeyUsage(decryptionKey);

        // 5. 복호화 토큰 생성
        String decryptionToken = generateDecryptionToken();

        log.info("키 검증 성공: keyId={}, cameraId={}, remainingUses={}",
                decryptionKey.getId(), requestDto.getCameraId(), decryptionKey.getRemainingUses());

        return decryptionKeyMapper.toKeyVerificationResponse(
                decryptionKey, true, "키 검증 성공", decryptionToken,
                requestDto.getCameraId(), blockchainValid);
    }

    @Override
    @Transactional
    public void revokeKey(KeyRevocationRequestDto requestDto, Long userId) {
        log.info("키 취소 요청: accessToken={}, userId={}", requestDto.getAccessToken(), userId);

        DecryptionKey decryptionKey = findKeyByAccessToken(requestDto.getAccessToken());
        validateKeyRevocation(decryptionKey, userId);

        String blockchainTxHash = revokeKeyOnBlockchain(decryptionKey.getKeyHash(), userId);
        updateKeyStatus(decryptionKey, "REVOKED", requestDto.getRevocationReason());

        log.info("키 취소 완료: keyId={}, blockchainTxHash={}", decryptionKey.getId(), blockchainTxHash);
    }

    @Override
    public BlockchainTransactionResponseDto getTransaction(String txHash) {
        BlockchainTransaction transaction = findTransactionByHash(txHash);
        return decryptionKeyMapper.toBlockchainTransactionResponse(transaction);
    }

    // ===== 키 생성 메서드 =====

    @Override
    public String generateCCTVDecryptionKey() {
        SecureRandom secureRandom = new SecureRandom();
        int keySize = decryptionConfig.getKey().getSize();
        byte[] keyBytes = new byte[keySize];
        secureRandom.nextBytes(keyBytes);
        return java.util.Base64.getEncoder().encodeToString(keyBytes);
    }

    @Override
    public String generateKeyHash(String rawKey) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawKey.getBytes("UTF-8"));
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("키 해시 생성 실패", e);
        }
    }

    @Override
    public String encryptKey(String rawKey) {
        return java.util.Base64.getEncoder().encodeToString(rawKey.getBytes());
    }

    @Override
    public String generateSecureToken() {
        SecureRandom secureRandom = new SecureRandom();
        int tokenSize = decryptionConfig.getSecurity().getTokenSize();
        byte[] tokenBytes = new byte[tokenSize];
        secureRandom.nextBytes(tokenBytes);
        return java.util.Base64.getEncoder().encodeToString(tokenBytes);
    }



    @Override
    public String generateDecryptionToken() {
        SecureRandom secureRandom = new SecureRandom();
        int tokenSize = decryptionConfig.getSecurity().getTokenSize();
        byte[] tokenBytes = new byte[tokenSize];
        secureRandom.nextBytes(tokenBytes);
        return java.util.Base64.getEncoder().encodeToString(tokenBytes);
    }

    @Override
    @Transactional
    public DecryptionKey createAndSaveDecryptionKey(Long userId, String encryptedKey, String keyHash, 
                                                   String blockchainTxHash, String accessToken) {
        DecryptionKey decryptionKey = decryptionKeyMapper.createDecryptionKey(
                userId, encryptedKey, keyHash, blockchainTxHash, accessToken,
                decryptionConfig.getKey().getType(),
                LocalDateTime.now().plusDays(decryptionConfig.getKey().getExpirationDays()),
                decryptionConfig.getKey().getDefaultUses()
        );
        
        return decryptionKeyRepository.save(decryptionKey);
    }

    // ===== 키 검증 메서드 =====

    @Override
    public ValidationResult validateKey(DecryptionKey decryptionKey, KeyVerificationRequestDto requestDto, Long userId) {
        // 1. 기본 토큰 검증 (가장 중요)
        if (!verifyAccessToken(decryptionKey, requestDto.getAccessToken())) {
            return DecryptionService.ValidationResult.failure("유효하지 않은 접근 토큰입니다.");
        }
        
        // 2. 키 소유자 확인
        if (!isKeyOwner(decryptionKey, userId)) {
            return DecryptionService.ValidationResult.failure("키 소유자가 아닙니다.");
        }
        
        // 3. 키 상태 확인 (활성 + 만료 + 사용횟수)
        if (!isKeyValid(decryptionKey)) {
            return DecryptionService.ValidationResult.failure("키가 유효하지 않습니다.");
        }

        return DecryptionService.ValidationResult.success();
    }

    /**
     * 사용자 ID와 접근 토큰 기반 키 검증
     */
    public ValidationResult validateKeyByUserIdAndToken(DecryptionKey decryptionKey, KeyVerificationRequestDto requestDto, Long userId) {
        // 1. 접근 토큰 검증
        if (!verifyAccessToken(decryptionKey, requestDto.getAccessToken())) {
            return DecryptionService.ValidationResult.failure("유효하지 않은 접근 토큰입니다.");
        }
        
        // 2. 키 소유자 확인
        if (!isKeyOwner(decryptionKey, userId)) {
            return DecryptionService.ValidationResult.failure("키 소유자가 아닙니다.");
        }
        
        // 3. 카메라 ID 검증 (선택적 - 향후 카메라별 권한 관리 가능)
        if (requestDto.getCameraId() != null && !requestDto.getCameraId().isEmpty()) {
            log.info("카메라 ID 검증: cameraId={}", requestDto.getCameraId());
        }
        
        // 4. 키 상태 확인 (활성 + 만료 + 사용횟수)
        if (!isKeyValid(decryptionKey)) {
            return DecryptionService.ValidationResult.failure("키가 유효하지 않습니다.");
        }

        return DecryptionService.ValidationResult.success();
    }

    public ValidationResult validateKeyByToken (DecryptionKey decryptionKey, KeyVerificationRequestDto requestDto){
        // 1. 접근 토큰 검증
        if(!verifyAccessToken(decryptionKey, requestDto.getAccessToken())) {
            return DecryptionService.ValidationResult.failure("유효하지 않은 접근 토큰입니다.");
        }

        // 2. 카메라 ID 검증
        if(requestDto.getCameraId() != null && !requestDto.getCameraId().isEmpty()) {
            log.info("카메라 ID 검증: cameraId={}", requestDto.getCameraId());
        }

        // 3. 키 상태 확인 (활성 + 만료 + 사용횟수)
        if(!isKeyValid(decryptionKey)) {
            return DecryptionService.ValidationResult.failure("키가 유효하지 않습니다.");
        }

        return DecryptionService.ValidationResult.success();
    }

    @Override
    public boolean verifyAccessToken(DecryptionKey decryptionKey, String accessToken) {
        return decryptionKey.getAccessToken().equals(accessToken);
    }

    @Override
    public boolean isKeyOwner(DecryptionKey decryptionKey, Long userId) {
        return decryptionKey.getUserId().equals(userId);
    }

    @Override
    public boolean isKeyActive(DecryptionKey decryptionKey) {
        return "ACTIVE".equals(decryptionKey.getStatus());
    }

    @Override
    public boolean isKeyExpired(DecryptionKey decryptionKey) {
        return decryptionKey.getExpiresAt() != null && 
               decryptionKey.getExpiresAt().isBefore(LocalDateTime.now());
    }

    @Override
    public boolean hasRemainingUses(DecryptionKey decryptionKey) {
        return decryptionKey.getRemainingUses() > 0;
    }

    /**
     * 키 유효성 종합 검증 (간소화)
     */
    public boolean isKeyValid(DecryptionKey decryptionKey) {
        return isKeyActive(decryptionKey) && 
               !isKeyExpired(decryptionKey) && 
               hasRemainingUses(decryptionKey);
    }

    @Override
    @Transactional
    public void updateKeyUsage(DecryptionKey decryptionKey) {
        decryptionKey.decrementRemainingUses();
        decryptionKeyRepository.save(decryptionKey);
    }

    @Override
    public void validateKeyRevocation(DecryptionKey decryptionKey, Long userId) {
        if (!isKeyOwner(decryptionKey, userId)) {
            throw new ApiException(ErrorCode.FORBIDDEN, "키 취소 권한이 없습니다.");
        }
        if (!isKeyActive(decryptionKey)) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "이미 취소되었거나 만료된 키입니다.");
        }
    }



    // ===== Private Helper Methods =====

    // 키 저장소 관련
    private DecryptionKey findKeyById(Long keyId) {
        return decryptionKeyRepository.findById(keyId)
                .orElseThrow(() -> new RuntimeException("키를 찾을 수 없습니다."));
    }

    /**
     * 사용자의 유효한 키 조회
     */
    private Optional<DecryptionKey> findValidKeyByUserId(Long userId) {
        return decryptionKeyRepository.findFirstByUserIdAndStatusAndExpiresAtAfterAndRemainingUsesGreaterThanOrderByIssuedAtDesc(
                userId, "ACTIVE", LocalDateTime.now(), 0);
    }

    private DecryptionKey findKeyByHash(String keyHash) {
        return decryptionKeyRepository.findByKeyHash(keyHash)
                .orElseThrow(() -> new RuntimeException("키를 찾을 수 없습니다."));
    }

    private DecryptionKey findKeyByAccessToken(String accessToken) {
        return decryptionKeyRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new RuntimeException("토큰을 찾을 수 없습니다."));
    }



    @Transactional
    private void updateKeyStatus(DecryptionKey decryptionKey, String status, String revocationReason) {
        DecryptionKey updatedKey = decryptionKeyMapper.createUpdatedDecryptionKey(decryptionKey, status, revocationReason);
        decryptionKeyRepository.save(updatedKey);
    }

    // 블록체인 관련
    private String registerKeyOnBlockchain(String keyHash, Long userId) {
        log.info("블록체인 설정 확인: enabled={}", decryptionConfig.getBlockchain().isEnabled());
        
        if (!decryptionConfig.getBlockchain().isEnabled()) {
            log.warn("블록체인이 비활성화되어 있습니다. 키 등록을 건너뜁니다.");
            return "BLOCKCHAIN_DISABLED";
        }
        
        log.info("블록체인에 키 등록: keyHash={}, userId={}", keyHash, userId);
        
        // 만료 시간과 사용 횟수 계산
        Long expiresAt = System.currentTimeMillis() / 1000 + (decryptionConfig.getKey().getExpirationDays() * 24 * 60 * 60);
        Integer remainingUses = decryptionConfig.getKey().getDefaultUses();
        String keyType = decryptionConfig.getKey().getType();
        
        String blockchainTxHash = blockchainService.registerKey(keyHash, userId, expiresAt, remainingUses, keyType);
        saveBlockchainTransaction(blockchainTxHash, "CCTV_KEY_ISSUANCE");
        return blockchainTxHash;
    }

    private String revokeKeyOnBlockchain(String keyHash, Long userId) {
        if (!decryptionConfig.getBlockchain().isEnabled()) {
            log.warn("블록체인이 비활성화되어 있습니다. 키 취소를 건너뜁니다.");
            return "BLOCKCHAIN_DISABLED";
        }
        
        log.info("블록체인에서 키 취소: keyHash={}, userId={}", keyHash, userId);
        String blockchainTxHash = blockchainService.revokeKey(keyHash, userId);
        saveBlockchainTransaction(blockchainTxHash, "KEY_REVOCATION");
        return blockchainTxHash;
    }

    private boolean isKeyValidOnBlockchain(DecryptionKey decryptionKey) {
        if (!decryptionConfig.getBlockchain().isEnabled()) {
            log.warn("블록체인이 비활성화되어 있습니다. 키 유효성을 확인할 수 없습니다.");
            return true;
        }
        
        // 블록체인에서 키 유효성 직접 확인
        boolean isValid = blockchainService.isKeyValid(decryptionKey.getKeyHash());
        log.info("블록체인 키 유효성 확인: keyHash={}, isValid={}", decryptionKey.getKeyHash(), isValid);
        return isValid;
    }

    @Transactional
    private void saveBlockchainTransaction(String txHash, String txType) {
        BlockchainTransaction transaction = decryptionKeyMapper.createBlockchainTransaction(txHash, txType);
        blockchainTransactionRepository.save(transaction);
    }

    private BlockchainTransaction findTransactionByHash(String txHash) {
        return blockchainTransactionRepository.findByTxHash(txHash)
                .orElseThrow(() -> new RuntimeException("트랜잭션을 찾을 수 없습니다."));
    }





    // 유틸리티
    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }


} 