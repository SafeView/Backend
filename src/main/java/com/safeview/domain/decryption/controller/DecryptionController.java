package com.safeview.domain.decryption.controller;

import com.safeview.domain.decryption.dto.*;
import com.safeview.domain.decryption.service.DecryptionService;
import com.safeview.global.config.ApiKeyValidator;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.safeview.global.response.ErrorCode.INVALID_API_KEY;

/**
 * 복호화 컨트롤러
 * 
 * CCTV 영상 복호화 관련 API를 제공합니다.
 * - 복호화 키 발급/검증/취소
 * - 키 목록 조회 및 상세 정보 조회
 * - 블록체인 트랜잭션 조회
 * - AI 서버 전용 키 검증 API
 * 
 * 보안: JWT 토큰 기반 인증, API 키 검증
 * 권한: MODERATOR, ADMIN 권한 필요
 */
@Slf4j
@RestController
@RequestMapping("/api/decryption")
@RequiredArgsConstructor
public class DecryptionController {

    private final DecryptionService decryptionService;
    private final ApiKeyValidator apiKeyValidator;

    /**
     * CCTV 복호화 키 발급 (MODERATOR, ADMIN만 가능)
     */
    @PostMapping("/keys")
    public ResponseEntity<ApiResponse<KeyIssuanceResponseDto>> issueKey(
            @AuthenticationPrincipal Long userId) {
        log.info("CCTV 복호화 키 발급 요청: userId={} (기존 유효 키 확인 후 발급)", userId);

        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
        }
        
        KeyIssuanceResponseDto response = decryptionService.issueKey(userId);
        
        return ResponseEntity.status(201)
                .body(ApiResponse.onSuccessWithMessage(response, "복호화 키가 성공적으로 발급되었습니다."));
    }

    /**
     * 키 목록 조회 (페이징) (MODERATOR, ADMIN만 가능)
     */
    @GetMapping("/keys")
    public ApiResponse<KeyListResponseDto> getKeyList(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("키 목록 조회 요청: userId={}, page={}, size={}", userId, page, size);
        
        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
        }
        
        KeyListResponseDto response = decryptionService.getKeyList(userId, page, size, sortBy, sortDir);
        
        return ApiResponse.onSuccess(response);
    }

    /**
     * 키 해시로 조회 (블록체인 검증용)
     */
    @GetMapping("/keys/hash/{keyHash}")
    public ApiResponse<KeyDetailResponseDto> getKeyByHash(@PathVariable String keyHash) {
        log.info("키 해시 조회 요청: keyHash={}", keyHash);
        
        // 키 해시 검증
        if (keyHash == null || keyHash.trim().isEmpty()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "키 해시를 입력해주세요.");
        }
        
        KeyDetailResponseDto response = decryptionService.getKeyByHash(keyHash);
        
        return ApiResponse.onSuccess(response);
    }

    /**
     * 복호화키 검증 (CCTV 모자이크 해제용) - 사용자 ID + 접근 토큰 + 카메라 ID로 검증 (MODERATOR, ADMIN만 가능)
     */
    @PostMapping("/keys/verify")
    public ResponseEntity<ApiResponse<KeyVerificationResponseDto>> verifyKey(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody KeyVerificationRequestDto requestDto) {
        
        log.info("키 검증 요청: userId={}, accessToken={}, cameraId={}", 
                userId, requestDto.getAccessToken(), requestDto.getCameraId());
        
        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
        }
        
        KeyVerificationResponseDto response = decryptionService.verifyKeyByUserIdAndToken(requestDto, userId);
        
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccessWithMessage(response, "키 검증이 완료되었습니다."));
    }

    /**
     * AI 서버 통신 전용 복호화키 검증 컨트롤러 -> AI 서버 전용 API KEY + 접근 토큰 + 카메랴 ID
     */
    @PostMapping("/keys/verify/ai")
    public ResponseEntity<ApiResponse<KeyVerificationResponseDto>> verifyKeyAI(
            @RequestHeader("AiApiKey") String aiApiKey,
            @Valid @RequestBody KeyVerificationRequestDto requestDto) {

        log.info("키 검증 요청: aiApiKey={}, accessToken={}, cameraId={}",
                aiApiKey, requestDto.getAccessToken(), requestDto.getCameraId());

        // API Key 검증
        if (!apiKeyValidator.isValidApiKey(aiApiKey)) {
            log.warn("Invalid API Key provided: {}", aiApiKey);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure(ErrorCode.INVALID_API_KEY));
        }

        KeyVerificationResponseDto response = decryptionService.verifyKeyByToken(requestDto);

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccessWithMessage(response, "키 검증이 완료되었습니다."));
    }

    /**
     * 키 취소 (MODERATOR, ADMIN만 가능)
     */
    @DeleteMapping("/keys")
    public ResponseEntity<ApiResponse<Void>> revokeKey(
            @Valid @RequestBody KeyRevocationRequestDto requestDto,
            @AuthenticationPrincipal Long userId) {
        
        log.info("키 취소 요청: accessToken={}, userId={}", requestDto.getAccessToken(), userId);
        
        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
        }
        
        decryptionService.revokeKey(requestDto, userId);
        
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccessWithMessage(null, "키가 성공적으로 취소되었습니다."));
    }

    /**
     * 블록체인 트랜잭션 조회 (MODERATOR, ADMIN만 가능)
     */
    @GetMapping("/transactions/{txHash}")
    public ApiResponse<BlockchainTransactionResponseDto> getTransaction(
            @PathVariable String txHash,
            @AuthenticationPrincipal Long userId) {
        log.info("블록체인 트랜잭션 조회 요청: txHash={}, userId={}", txHash, userId);
        
        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
        }
        
        // 트랜잭션 해시 검증
        if (txHash == null || txHash.trim().isEmpty()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "트랜잭션 해시를 입력해주세요.");
        }
        
        BlockchainTransactionResponseDto response = decryptionService.getTransaction(txHash, userId);
        
        return ApiResponse.onSuccess(response);
    }
} 