package com.safeview.domain.decryption.controller;

import com.safeview.domain.decryption.service.BlockchainService;
import com.safeview.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 블록체인 컨트롤러
 * 
 * 블록체인 관련 API를 제공합니다.
 * - 블록체인 연결 상태 확인
 * - 계정 잔액 조회
 * - 키 정보 조회 및 유효성 확인
 * - 키 사용 및 만료 처리
 * - 긴급 키 취소
 * - 컨트랙트 소유권 관리
 * 
 * 보안: 블록체인 기반 키 관리
 * 네트워크: Sepolia 테스트넷 지원
 */
@Slf4j
@RestController
@RequestMapping("/api/blockchain")
@RequiredArgsConstructor
public class BlockchainController {

    private final BlockchainService blockchainService;

    /**
     * 블록체인 연결 상태 확인
     */
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> checkHealth() {
        boolean isConnected = blockchainService.isConnected();
        BigInteger networkId = blockchainService.getNetworkId();
        String contractAddress = blockchainService.getContractAddress();
        
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("connected", isConnected);
        healthInfo.put("networkId", networkId);
        healthInfo.put("contractAddress", contractAddress);
        healthInfo.put("timestamp", System.currentTimeMillis());
        
        return ApiResponse.onSuccess(healthInfo);
    }

    /**
     * 계정 잔액 조회
     */
    @GetMapping("/balance/{address}")
    public ApiResponse<Map<String, Object>> getBalance(@PathVariable String address) {
        try {
            BigInteger balance = blockchainService.getBalance(address);
            
            Map<String, Object> balanceInfo = new HashMap<>();
            balanceInfo.put("address", address);
            balanceInfo.put("balanceWei", balance);
            balanceInfo.put("balanceEth", balance.divide(BigInteger.valueOf(10).pow(18)));
            
            return ApiResponse.onSuccess(balanceInfo);
        } catch (Exception e) {
            log.error("잔액 조회 실패: address={}, error={}", address, e.getMessage());
            return ApiResponse.onFailure("BLOCKCHAIN_ERROR", "잔액 조회에 실패했습니다.", null);
        }
    }

    /**
     * 키 정보 조회
     */
    @GetMapping("/keys/{keyHash}")
    public ApiResponse<BlockchainService.KeyInfo> getKeyInfo(@PathVariable String keyHash) {
        try {
            BlockchainService.KeyInfo keyInfo = blockchainService.getKeyInfo(keyHash);
            return ApiResponse.onSuccess(keyInfo);
        } catch (Exception e) {
            log.error("키 정보 조회 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return ApiResponse.onFailure("BLOCKCHAIN_ERROR", "키 정보 조회에 실패했습니다.", null);
        }
    }

    /**
     * 키 유효성 확인
     */
    @GetMapping("/keys/{keyHash}/valid")
    public ApiResponse<Map<String, Object>> checkKeyValidity(@PathVariable String keyHash) {
        try {
            boolean isValid = blockchainService.isKeyValid(keyHash);
            boolean isRegistered = blockchainService.isKeyRegistered(keyHash);
            boolean isRevoked = blockchainService.isKeyRevoked(keyHash);
            
            Map<String, Object> validityInfo = new HashMap<>();
            validityInfo.put("keyHash", keyHash);
            validityInfo.put("isValid", isValid);
            validityInfo.put("isRegistered", isRegistered);
            validityInfo.put("isRevoked", isRevoked);
            
            return ApiResponse.onSuccess(validityInfo);
        } catch (Exception e) {
            log.error("키 유효성 확인 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return ApiResponse.onFailure("BLOCKCHAIN_ERROR", "키 유효성 확인에 실패했습니다.", null);
        }
    }

    /**
     * 키 소유자 조회
     */
    @GetMapping("/keys/{keyHash}/owner")
    public ApiResponse<Map<String, Object>> getKeyOwner(@PathVariable String keyHash) {
        try {
            String owner = blockchainService.getKeyOwner(keyHash);
            
            Map<String, Object> ownerInfo = new HashMap<>();
            ownerInfo.put("keyHash", keyHash);
            ownerInfo.put("owner", owner);
            
            return ApiResponse.onSuccess(ownerInfo);
        } catch (Exception e) {
            log.error("키 소유자 조회 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return ApiResponse.onFailure("BLOCKCHAIN_ERROR", "키 소유자 조회에 실패했습니다.", null);
        }
    }

    /**
     * 사용자의 키 목록 조회
     */
    @GetMapping("/users/{userAddress}/keys")
    public ApiResponse<List<String>> getUserKeys(@PathVariable String userAddress) {
        try {
            List<String> keys = blockchainService.getUserKeys(userAddress);
            return ApiResponse.onSuccess(keys);
        } catch (Exception e) {
            log.error("사용자 키 목록 조회 실패: userAddress={}, error={}", userAddress, e.getMessage());
            return ApiResponse.onFailure("BLOCKCHAIN_ERROR", "사용자 키 목록 조회에 실패했습니다.", null);
        }
    }

    /**
     * 사용자 ID로 키 목록 조회
     */
    @GetMapping("/users/id/{userId}/keys")
    public ApiResponse<List<String>> getKeysByUserId(@PathVariable Long userId) {
        try {
            List<String> keys = blockchainService.getKeysByUserId(userId);
            return ApiResponse.onSuccess(keys);
        } catch (Exception e) {
            log.error("사용자 ID 키 목록 조회 실패: userId={}, error={}", userId, e.getMessage());
            return ApiResponse.onFailure("BLOCKCHAIN_ERROR", "사용자 ID 키 목록 조회에 실패했습니다.", null);
        }
    }

    /**
     * 키 사용 (사용 횟수 감소)
     */
    @PostMapping("/keys/{keyHash}/use")
    public ApiResponse<Map<String, Object>> useKey(@PathVariable String keyHash) {
        try {
            String txHash = blockchainService.useKey(keyHash);
            
            Map<String, Object> useInfo = new HashMap<>();
            useInfo.put("keyHash", keyHash);
            useInfo.put("transactionHash", txHash);
            useInfo.put("message", "키가 성공적으로 사용되었습니다.");
            
            return ApiResponse.onSuccess(useInfo);
        } catch (Exception e) {
            log.error("키 사용 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return ApiResponse.onFailure("BLOCKCHAIN_ERROR", "키 사용에 실패했습니다.", null);
        }
    }

    /**
     * 만료된 키 정리
     */
    @PostMapping("/keys/{keyHash}/expire")
    public ApiResponse<Map<String, Object>> expireKey(@PathVariable String keyHash) {
        try {
            String txHash = blockchainService.expireKey(keyHash);
            
            Map<String, Object> expireInfo = new HashMap<>();
            expireInfo.put("keyHash", keyHash);
            expireInfo.put("transactionHash", txHash);
            expireInfo.put("message", "키가 성공적으로 만료 처리되었습니다.");
            
            return ApiResponse.onSuccess(expireInfo);
        } catch (Exception e) {
            log.error("키 만료 처리 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return ApiResponse.onFailure("BLOCKCHAIN_ERROR", "키 만료 처리에 실패했습니다.", null);
        }
    }

    /**
     * 긴급 키 취소 (관리자만)
     */
    @PostMapping("/keys/{keyHash}/emergency-revoke")
    public ApiResponse<Map<String, Object>> emergencyRevokeKey(@PathVariable String keyHash) {
        try {
            String txHash = blockchainService.emergencyRevokeKey(keyHash);
            
            Map<String, Object> revokeInfo = new HashMap<>();
            revokeInfo.put("keyHash", keyHash);
            revokeInfo.put("transactionHash", txHash);
            revokeInfo.put("message", "키가 긴급 취소되었습니다.");
            
            return ApiResponse.onSuccess(revokeInfo);
        } catch (Exception e) {
            log.error("긴급 키 취소 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return ApiResponse.onFailure("BLOCKCHAIN_ERROR", "긴급 키 취소에 실패했습니다.", null);
        }
    }

    /**
     * 컨트랙트 소유자 변경 (관리자만)
     */
    @PostMapping("/ownership/transfer")
    public ApiResponse<Map<String, Object>> transferOwnership(@RequestParam String newOwner) {
        try {
            String txHash = blockchainService.transferOwnership(newOwner);
            
            Map<String, Object> transferInfo = new HashMap<>();
            transferInfo.put("newOwner", newOwner);
            transferInfo.put("transactionHash", txHash);
            transferInfo.put("message", "소유권이 성공적으로 변경되었습니다.");
            
            return ApiResponse.onSuccess(transferInfo);
        } catch (Exception e) {
            log.error("소유권 변경 실패: newOwner={}, error={}", newOwner, e.getMessage());
            return ApiResponse.onFailure("BLOCKCHAIN_ERROR", "소유권 변경에 실패했습니다.", null);
        }
    }

    /**
     * 블록체인 통계 조회
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getBlockchainStats() {
        try {
            boolean isConnected = blockchainService.isConnected();
            BigInteger networkId = blockchainService.getNetworkId();
            String contractAddress = blockchainService.getContractAddress();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("connected", isConnected);
            stats.put("networkId", networkId);
            stats.put("contractAddress", contractAddress);
            stats.put("networkName", networkId.equals(BigInteger.valueOf(11155111)) ? "Sepolia Testnet" : "Unknown");
            stats.put("timestamp", System.currentTimeMillis());
            
            return ApiResponse.onSuccess(stats);
        } catch (Exception e) {
            log.error("블록체인 통계 조회 실패: error={}", e.getMessage());
            return ApiResponse.onFailure("BLOCKCHAIN_ERROR", "블록체인 통계 조회에 실패했습니다.", null);
        }
    }
} 