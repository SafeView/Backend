package com.safeview.domain.decryption.repository;

import com.safeview.domain.decryption.entity.DecryptionKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DecryptionKeyRepository extends JpaRepository<DecryptionKey, Long> {

    // 키 해시로 조회 (블록체인 검증용)
    Optional<DecryptionKey> findByKeyHash(String keyHash);
    
    // 접근 토큰으로 조회 (일회성 토큰 검증용)
    Optional<DecryptionKey> findByAccessToken(String accessToken);
    
    // 사용자별 키 페이징 조회
    Page<DecryptionKey> findByUserId(Long userId, Pageable pageable);
    
    // 키 존재 여부 확인
    boolean existsByKeyHash(String keyHash);
    
    // 사용자의 유효한 키 조회 (ACTIVE 상태이고 만료되지 않았으며 사용 횟수가 남은 키)
    Optional<DecryptionKey> findFirstByUserIdAndStatusAndExpiresAtAfterAndRemainingUsesGreaterThanOrderByIssuedAtDesc(
            Long userId, String status, LocalDateTime now, int remainingUses);
} 