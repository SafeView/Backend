package com.safeview.domain.decryption.repository;

import com.safeview.domain.decryption.entity.DecryptionKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    // ===== 대시보드 통계용 메서드들 =====
    
    /**
     * 특정 상태의 키 수 조회
     * 
     * @param status 키 상태 (ACTIVE, EXPIRED, REVOKED)
     * @return 해당 상태의 키 수
     */
    long countByStatus(String status);
    
    /**
     * 특정 기간 동안 발급된 키 수 조회
     * 
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간 동안 발급된 키 수
     */
    @Query("SELECT COUNT(dk) FROM DecryptionKey dk WHERE dk.issuedAt BETWEEN :startDate AND :endDate")
    long countByIssuedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 월에 발급된 키 수 조회
     * 
     * @param year 연도
     * @param month 월 (1-12)
     * @return 해당 월에 발급된 키 수
     */
    @Query("SELECT COUNT(dk) FROM DecryptionKey dk WHERE YEAR(dk.issuedAt) = :year AND MONTH(dk.issuedAt) = :month")
    long countByIssuedAtYearAndMonth(@Param("year") int year, @Param("month") int month);
    
    /**
     * 총 발급된 사용 횟수 조회
     * 기본 사용 횟수(90) * 총 키 수로 계산
     * 
     * @return 총 발급된 사용 횟수
     */
    @Query("SELECT COUNT(dk) * 90 FROM DecryptionKey dk")
    Long getTotalIssuedUses();
    
    /**
     * 총 사용된 횟수 조회
     * (기본 사용 횟수 - 현재 남은 사용 횟수)의 합
     * 
     * @return 총 사용된 횟수
     */
    @Query("SELECT SUM(90 - dk.remainingUses) FROM DecryptionKey dk WHERE dk.lastUsedAt IS NOT NULL")
    Long getTotalUsedUses();
} 