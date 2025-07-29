package com.safeview.domain.decryption.service;

/**
 * 블록체인 연동 서비스 인터페이스
 */
public interface BlockchainService {
    
    /**
     * 키 해시를 블록체인에 등록
     * @param keyHash 키 해시
     * @param userId 사용자 ID
     * @return 트랜잭션 해시
     */
    String registerKey(String keyHash, Long userId);
    
    /**
     * 키를 블록체인에서 취소
     * @param keyHash 키 해시
     * @param userId 사용자 ID
     * @return 트랜잭션 해시
     */
    String revokeKey(String keyHash, Long userId);
    
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
} 