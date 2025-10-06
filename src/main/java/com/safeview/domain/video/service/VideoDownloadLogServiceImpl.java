package com.safeview.domain.video.service;

import com.safeview.domain.user.entity.Role;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.repository.UserRepository;
import com.safeview.domain.video.dto.VideoDownloadLogResponseDto;
import com.safeview.domain.video.entity.VideoDownloadLog;
import com.safeview.domain.video.mapper.VideoDownloadLogMapper;
import com.safeview.domain.video.repository.VideoDownloadLogRepository;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 영상 다운로드 로그 서비스 구현체
 * 
 * 영상 다운로드 로그 관련 비즈니스 로직을 담당하는 서비스 구현
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VideoDownloadLogServiceImpl implements VideoDownloadLogService {
    
    private final VideoDownloadLogRepository videoDownloadLogRepository;
    private final VideoDownloadLogMapper videoDownloadLogMapper;
    private final UserRepository userRepository;
    
    /**
     * 다운로드 로그 기록
     * 
     * @param userId 사용자 ID
     * @param userName 사용자 이름
     * @return 저장된 다운로드 로그
     */
    @Override
    public VideoDownloadLog logDownload(Long userId, String userName) {
        log.info("다운로드 로그 기록: userId={}, userName={}", userId, userName);
        
        // 사용자 전체 정보 조회 및 Role이 ADMIN인지 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED, "사용자 정보를 찾을 수 없습니다."));
        
        // 매퍼를 통해 사용자 전체 정보로 다운로드 로그 생성 및 저장
        VideoDownloadLog downloadLog = videoDownloadLogMapper.createDownloadLog(user);
        VideoDownloadLog savedLog = videoDownloadLogRepository.save(downloadLog);
        
        log.info("다운로드 로그 기록 완료: logId={}, userEmail={}, userRole={}", savedLog.getId(), user.getEmail(), user.getRole());
        return savedLog;
    }
    
    /**
     * 관리자용 다운로드 로그 조회 (페이지네이션)
     * 
     * @param adminUserId 관리자 사용자 ID
     * @param pageable 페이징 정보
     * @return 다운로드 로그 목록 (페이지네이션)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VideoDownloadLogResponseDto> getDownloadLogsForAdmin(Long adminUserId, Pageable pageable) {
        log.info("관리자 다운로드 로그 조회: adminUserId={}, page={}, size={}", adminUserId, pageable.getPageNumber(), pageable.getPageSize());
        
        // 관리자 권한 검증
        User user = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED, "사용자 정보를 찾을 수 없습니다."));
        
        if (user.getRole() != Role.ADMIN) {
            throw new ApiException(ErrorCode.FORBIDDEN, "관리자 권한이 필요합니다.");
        }
        
        // 최근 다운로드 로그 조회
        Page<VideoDownloadLog> downloadLogs = videoDownloadLogRepository.findRecentDownloads(pageable);
        
        // DTO로 변환
        Page<VideoDownloadLogResponseDto> responseDtoPage = downloadLogs.map(videoDownloadLogMapper::toResponseDto);
        
        log.info("관리자 다운로드 로그 조회 완료: adminUserId={}, totalElements={}, totalPages={}", 
                adminUserId, responseDtoPage.getTotalElements(), responseDtoPage.getTotalPages());
        return responseDtoPage;
    }
    
}
