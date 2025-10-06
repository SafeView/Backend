package com.safeview.domain.video.service;

import com.safeview.domain.video.dto.VideoDownloadLogResponseDto;
import com.safeview.domain.video.entity.VideoDownloadLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * 영상 다운로드 로그 서비스 인터페이스
 * 
 * 영상 다운로드 로그 관련 비즈니스 로직을 담당하는 서비스
 */
public interface VideoDownloadLogService {
    
    /**
     * 다운로드 로그 기록
     * 
     * @param userId 사용자 ID
     * @param userName 사용자 이름
     * @return 저장된 다운로드 로그
     */
    VideoDownloadLog logDownload(Long userId, String userName);
    
    /**
     * 관리자용 다운로드 로그 조회 (페이지네이션)
     * 
     * @param adminUserId 관리자 사용자 ID
     * @param pageable 페이징 정보
     * @return 다운로드 로그 목록 (페이지네이션)
     */
    Page<VideoDownloadLogResponseDto> getDownloadLogsForAdmin(Long adminUserId, Pageable pageable);
}
