package com.safeview.domain.decryption.service;

import java.math.BigInteger;
import java.util.List;

/**
 * 블록체인 연동 서비스 인터페이스
 * 
 * 블록체인과의 연동을 담당하는 서비스입니다.
 * - 키 등록/취소/사용 관리
 * - 키 유효성 검증
 * - 블록체인 상태 조회
 * - 관리자 기능 (긴급 취소, 소유권 변경)
 * 
 * 보안: 블록체인 기반 키 무결성 보장
 * 네트워크: Sepolia 테스트넷 지원
 */
public interface BlockchainService {
    
    // ===== 키 관리 =====
    
    /**
     * 키 해시를 블록체인에 등록
     * @param keyHash 키 해시
     * @param userId 사용자 ID
     * @param expiresAt 만료 시간
     * @param remainingUses 남은 사용 횟수
     * @param keyType 키 타입
     * @return 트랜잭션 해시
     */
    String registerKey(String keyHash, Long userId, Long expiresAt, Integer remainingUses, String keyType);
    
    /**
     * 키를 블록체인에서 취소
     * @param keyHash 키 해시
     * @param userId 사용자 ID
     * @return 트랜잭션 해시
     */
    String revokeKey(String keyHash, Long userId);
    
    /**
     * 키 사용 (사용 횟수 감소)
     * @param keyHash 키 해시
     * @return 트랜잭션 해시
     */
    String useKey(String keyHash);
    
    /**
     * 만료된 키 정리
     * @param keyHash 키 해시
     * @return 트랜잭션 해시
     */
    String expireKey(String keyHash);
    
    // ===== 키 조회 =====
    
    /**
     * 키 해시가 블록체인에 등록되어 있는지 확인
     * @param keyHash 키 해시
     * @return 등록 여부
     */
    boolean isKeyRegistered(String keyHash);
    
    /**
     * 키 해시가 취소되었는지 확인
     * @param keyHash 키 해시
     * @return 취소 여부
     */
    boolean isKeyRevoked(String keyHash);
    
    /**
     * 키 유효성 확인 (활성 + 미취소 + 미만료 + 사용횟수 > 0)
     * @param keyHash 키 해시
     * @return 유효성 여부
     */
    boolean isKeyValid(String keyHash);
    
    /**
     * 키 정보 조회
     * @param keyHash 키 해시
     * @return 키 정보
     */
    KeyInfo getKeyInfo(String keyHash);
    
    /**
     * 키 소유자 조회
     * @param keyHash 키 해시
     * @return 소유자 주소
     */
    String getKeyOwner(String keyHash);
    
    // ===== 사용자별 조회 =====
    
    /**
     * 사용자의 키 목록 조회
     * @param userAddress 사용자 주소
     * @return 키 해시 목록
     */
    List<String> getUserKeys(String userAddress);
    
    /**
     * 사용자 ID로 키 목록 조회
     * @param userId 사용자 ID
     * @return 키 해시 목록
     */
    List<String> getKeysByUserId(Long userId);
    
    // ===== 관리자 기능 =====
    
    /**
     * 긴급 키 취소 (관리자만)
     * @param keyHash 키 해시
     * @return 트랜잭션 해시
     */
    String emergencyRevokeKey(String keyHash);
    
    /**
     * 컨트랙트 소유자 변경
     * @param newOwner 새로운 소유자 주소
     * @return 트랜잭션 해시
     */
    String transferOwnership(String newOwner);
    
    // ===== 블록체인 상태 =====
    
    /**
     * 블록체인 연결 상태 확인
     * @return 연결 상태
     */
    boolean isConnected();
    
    /**
     * 네트워크 ID 조회
     * @return 네트워크 ID
     */
    BigInteger getNetworkId();
    
    /**
     * 계정 잔액 조회
     * @param address 계정 주소
     * @return 잔액 (Wei)
     */
    BigInteger getBalance(String address);
    
    /**
     * 컨트랙트 주소 조회
     * @return 컨트랙트 주소
     */
    String getContractAddress();
    
    // ===== 키 정보 DTO =====
    
    /**
     * 블록체인 키 정보
     */
    class KeyInfo {
        private String owner;
        private Long userId;
        private Long issuedAt;
        private Long expiresAt;
        private Integer remainingUses;
        private Boolean isActive;
        private Boolean isRevoked;
        private String keyType;
        
        // 생성자, getter, setter 생략...
        
        public KeyInfo() {}
        
        public KeyInfo(String owner, Long userId, Long issuedAt, Long expiresAt, 
                      Integer remainingUses, Boolean isActive, Boolean isRevoked, String keyType) {
            this.owner = owner;
            this.userId = userId;
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
            this.remainingUses = remainingUses;
            this.isActive = isActive;
            this.isRevoked = isRevoked;
            this.keyType = keyType;
        }
        
        // Getter methods
        public String getOwner() { return owner; }
        public Long getUserId() { return userId; }
        public Long getIssuedAt() { return issuedAt; }
        public Long getExpiresAt() { return expiresAt; }
        public Integer getRemainingUses() { return remainingUses; }
        public Boolean getIsActive() { return isActive; }
        public Boolean getIsRevoked() { return isRevoked; }
        public String getKeyType() { return keyType; }
        
        // Setter methods
        public void setOwner(String owner) { this.owner = owner; }
        public void setUserId(Long userId) { this.userId = userId; }
        public void setIssuedAt(Long issuedAt) { this.issuedAt = issuedAt; }
        public void setExpiresAt(Long expiresAt) { this.expiresAt = expiresAt; }
        public void setRemainingUses(Integer remainingUses) { this.remainingUses = remainingUses; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
        public void setIsRevoked(Boolean isRevoked) { this.isRevoked = isRevoked; }
        public void setKeyType(String keyType) { this.keyType = keyType; }
    }
} 