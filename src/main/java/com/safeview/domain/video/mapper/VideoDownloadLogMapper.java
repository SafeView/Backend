package com.safeview.domain.video.mapper;

import com.safeview.domain.user.entity.User;
import com.safeview.domain.video.dto.VideoDownloadLogResponseDto;
import com.safeview.domain.video.entity.VideoDownloadLog;
import org.springframework.stereotype.Component;

/**
 * 영상 다운로드 로그 매퍼
 * 
 * 영상 다운로드 로그 관련 변환 로직을 담당하는 매퍼
 */
@Component
public class VideoDownloadLogMapper {
    
    /**
     * 다운로드 로그 생성 (사용자 전체 정보 포함)
     * 
     * @param user 사용자 엔티티
     * @return VideoDownloadLog 인스턴스
     */
    public VideoDownloadLog createDownloadLog(User user) {
        return VideoDownloadLog.builder()
                .userId(user.getId())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .userPhone(user.getPhone())
                .userAddress(user.getAddress())
                .userGender(user.getGender() != null ? user.getGender().name() : null)
                .userBirthday(user.getBirthday())
                .userRole(user.getRole() != null ? user.getRole().name() : null)
                .build();
    }
    
    /**
     * 다운로드 로그 생성 (기본 정보만)
     * 
     * @param userId 사용자 ID
     * @param userName 사용자 이름
     * @return VideoDownloadLog 인스턴스
     */
    public VideoDownloadLog createDownloadLog(Long userId, String userName) {
        return VideoDownloadLog.builder()
                .userId(userId)
                .userName(userName)
                .build();
    }
    
    /**
     * VideoDownloadLog 엔티티를 DTO로 변환
     * 
     * @param log VideoDownloadLog 엔티티
     * @return VideoDownloadLogResponseDto
     */
    public VideoDownloadLogResponseDto toResponseDto(VideoDownloadLog log) {
        return VideoDownloadLogResponseDto.builder()
                .logId(log.getId())
                .userId(log.getUserId())
                .userName(log.getUserName())
                .userEmail(log.getUserEmail())
                .userPhone(log.getUserPhone())
                .userAddress(log.getUserAddress())
                .userGender(log.getUserGender())
                .userBirthday(log.getUserBirthday())
                .userRole(log.getUserRole())
                .downloadTime(log.getCreatedAt())
                .build();
    }
}
