package com.safeview.domain.user.repository;

import com.safeview.domain.user.entity.Role;
import com.safeview.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndIdNot(String phone, Long id);
    
    /**
     * 특정 역할의 사용자 수 조회
     */
    long countByRole(Role role);
    
    /**
     * 특정 기간 동안 가입한 사용자 수 조회
     * 
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간 동안 가입한 사용자 수
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 월에 가입한 사용자 수 조회
     * 
     * @param year 연도
     * @param month 월 (1-12)
     * @return 해당 월에 가입한 사용자 수
     */
    @Query("SELECT COUNT(u) FROM User u WHERE YEAR(u.createdAt) = :year AND MONTH(u.createdAt) = :month")
    long countByCreatedAtYearAndMonth(@Param("year") int year, @Param("month") int month);
}