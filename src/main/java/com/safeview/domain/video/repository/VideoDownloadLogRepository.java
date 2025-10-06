package com.safeview.domain.video.repository;

import com.safeview.domain.video.entity.VideoDownloadLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * 영상 다운로드 로그 리포지토리
 * 
 * 영상 다운로드 로그 관련 데이터베이스 작업을 담당하는 리포지토리
 */
public interface VideoDownloadLogRepository extends JpaRepository<VideoDownloadLog, Long> {
    
    /**
     * 최근 다운로드 로그 조회 (관리자용, 페이지네이션)
     * 
     * @param pageable 페이징 정보
     * @return 최근 다운로드 로그 목록
     */
    @Query("SELECT vdl FROM VideoDownloadLog vdl ORDER BY vdl.createdAt DESC")
    Page<VideoDownloadLog> findRecentDownloads(Pageable pageable);
    
}
