package com.safeview.domain.decryption.controller;

import com.safeview.domain.decryption.dto.*;
import com.safeview.domain.decryption.service.DecryptionService;
import com.safeview.global.config.ApiKeyValidator;
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
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<KeyIssuanceResponseDto>> issueKey(
            @AuthenticationPrincipal Long userId) {
        
        log.info("CCTV 복호화 키 발급 요청: userId={} (기존 유효 키 확인 후 발급)", userId);
        
        KeyIssuanceResponseDto response = decryptionService.issueKey(userId);
        
        return ResponseEntity.status(201)
                .body(ApiResponse.onSuccessWithMessage(response, "복호화 키가 성공적으로 발급되었습니다."));
    }

    /**
     * 키 목록 조회 (페이징) (MODERATOR, ADMIN만 가능)
     */
    @GetMapping("/keys")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ApiResponse<KeyListResponseDto> getKeyList(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("키 목록 조회 요청: userId={}, page={}, size={}", userId, page, size);
        
        KeyListResponseDto response = decryptionService.getKeyList(userId, page, size, sortBy, sortDir);
        
        return ApiResponse.onSuccess(response);
    }

    /**
     * 키 해시로 조회 (블록체인 검증용)
     */
    @GetMapping("/keys/hash/{keyHash}")
    public ApiResponse<KeyDetailResponseDto> getKeyByHash(@PathVariable String keyHash) {
        log.info("키 해시 조회 요청: keyHash={}", keyHash);
        
        KeyDetailResponseDto response = decryptionService.getKeyByHash(keyHash);
        
        return ApiResponse.onSuccess(response);
    }

    /**
     * 복호화키 검증 (CCTV 모자이크 해제용) - 사용자 ID + 접근 토큰 + 카메라 ID로 검증 (MODERATOR, ADMIN만 가능)
     */
    @PostMapping("/keys/verify")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<KeyVerificationResponseDto>> verifyKey(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody KeyVerificationRequestDto requestDto) {
        
        log.info("키 검증 요청: userId={}, accessToken={}, cameraId={}", 
                userId, requestDto.getAccessToken(), requestDto.getCameraId());
        
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
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> revokeKey(
            @Valid @RequestBody KeyRevocationRequestDto requestDto,
            @AuthenticationPrincipal Long userId) {
        
        log.info("키 취소 요청: accessToken={}, userId={}", requestDto.getAccessToken(), userId);
        
        decryptionService.revokeKey(requestDto, userId);
        
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccessWithMessage(null, "키가 성공적으로 취소되었습니다."));
    }

    /**
     * 블록체인 트랜잭션 조회 (MODERATOR, ADMIN만 가능)
     */
    @GetMapping("/transactions/{txHash}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<BlockchainTransactionResponseDto> getTransaction(@PathVariable String txHash) {
        log.info("블록체인 트랜잭션 조회 요청: txHash={}", txHash);
        
        BlockchainTransactionResponseDto response = decryptionService.getTransaction(txHash);
        
        return ApiResponse.onSuccess(response);
    }
} 