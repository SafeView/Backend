package com.safeview.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 영상 다운로드 로그 응답 DTO
 * 
 * 다운로드 로그 조회 시 반환하는 데이터 전송 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDownloadLogResponseDto {
    
    /**
     * 다운로드 로그 ID
     */
    private Long logId;
    
    /**
     * 사용자 ID
     */
    private Long userId;
    
    /**
     * 사용자 이름
     */
    private String userName;
    
    /**
     * 사용자 이메일
     */
    private String userEmail;
    
    /**
     * 사용자 전화번호
     */
    private String userPhone;
    
    /**
     * 사용자 주소
     */
    private String userAddress;
    
    /**
     * 사용자 성별
     */
    private String userGender;
    
    /**
     * 사용자 생년월일
     */
    private String userBirthday;
    
    /**
     * 사용자 역할
     */
    private String userRole;
    
    /**
     * 다운로드 시간
     */
    private LocalDateTime downloadTime;
    
}
