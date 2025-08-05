package com.safeview.domain.administrator.repository;

import com.safeview.domain.administrator.entity.AdminRequest;
import com.safeview.domain.administrator.entity.AdminRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {

    // 사용자별 요청 조회 (페이지네이션 없음)
    List<AdminRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 사용자별 요청 조회 (페이지네이션 있음)
    Page<AdminRequest> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 상태별 요청 조회 (페이지네이션 없음)
    List<AdminRequest> findByStatusOrderByCreatedAtDesc(AdminRequestStatus status);

    // 상태별 요청 조회 (페이지네이션 있음)
    Page<AdminRequest> findByStatusOrderByCreatedAtDesc(AdminRequestStatus status, Pageable pageable);

    // 사용자별 상태별 요청 조회
    Page<AdminRequest> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, AdminRequestStatus status, Pageable pageable);

    // 대기중인 요청 개수 조회
    @Query("SELECT COUNT(a) FROM AdminRequest a WHERE a.status = 'PENDING'")
    long countPendingRequests();

    // 사용자별 대기중인 요청 개수 조회
    @Query("SELECT COUNT(a) FROM AdminRequest a WHERE a.userId = :userId AND a.status = 'PENDING'")
    long countPendingRequestsByUserId(@Param("userId") Long userId);

    // 최근 요청 조회 (최신 10개)
    List<AdminRequest> findTop10ByOrderByCreatedAtDesc();

    // 모든 요청 조회 (최신순)
    List<AdminRequest> findAllByOrderByCreatedAtDesc();
} 