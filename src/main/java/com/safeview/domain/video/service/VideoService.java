package com.safeview.domain.video.service;

import com.safeview.domain.video.dto.DownloadResponseDto;
import com.safeview.domain.video.dto.RecordingResponseDto;
import com.safeview.domain.video.dto.VideoListResponseDto;
import com.safeview.domain.video.dto.VideoResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 비디오 서비스 인터페이스
 * 
 * CCTV 영상 관련 비즈니스 로직을 담당합니다.
 * - 영상 녹화 시작/중지
 * - 비디오 엔티티 생성 및 관리
 * - 영상 목록 조회 (사용자별, 관리자용)
 * - 영상 다운로드
 * 
 * 보안: 사용자별 영상 접근 권한 검증
 * 감사: 영상 생성 및 조회 이력 관리
 */
@Service
public interface VideoService {
    RecordingResponseDto startRecording ();
    RecordingResponseDto stopRecording(Long userId);
    List<VideoResponseDto> getAllVideosByUserId(Long userId);
    DownloadResponseDto downloadVideo(String filename);
    ResponseEntity<byte[]> streamVideo(String filename, String rangeHeader);
    List<VideoListResponseDto> getAllVideosGroupedByUser();
    void makeVideoEntity(List<String>urls, Long userId);
}
