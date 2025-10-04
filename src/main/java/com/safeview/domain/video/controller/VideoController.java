package com.safeview.domain.video.controller;


import com.safeview.domain.video.dto.*;
import com.safeview.domain.video.service.VideoService;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.ErrorCode;
import com.safeview.global.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 비디오 컨트롤러
 * 
 * CCTV 영상 관련 API를 제공합니다.
 * - 영상 녹화 시작/중지
 * - 비디오 엔티티 생성
 * - 영상 목록 조회 (사용자별, 관리자용)
 * - 영상 다운로드
 * 
 * 보안: JWT 토큰 기반 인증, 권한별 접근 제어
 * 권한: 일반 사용자 (자신의 영상만), 관리자 (전체 영상 조회)
 */
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final VideoService videoService;

    /**
     * 영상 녹화 시작
     * 
     * @param userId 인증된 사용자 ID
     * @return 녹화 시작 응답 정보
     * 
     * 처리 과정:
     * 1. 영상 녹화 시작 요청
     * 2. 녹화 상태 및 정보 반환
     * 
     * 보안: 인증된 사용자만 접근 가능
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<RecordingResponseDto>> startRecording (@AuthenticationPrincipal Long userId) {
        log.info("영상 녹화 시작 요청: userId={}", userId);
        
        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
        }
        
        RecordingResponseDto responseDto = videoService.startRecording();
        
        log.info("영상 녹화 시작 완료: userId={}", userId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, responseDto);
    }

    /**
     * 영상 녹화 중지
     * 
     * @param userId 인증된 사용자 ID
     * @return 녹화 중지 응답 정보
     * 
     * 처리 과정:
     * 1. 영상 녹화 중지 요청
     * 2. 녹화된 영상 정보 반환
     * 
     * 보안: 인증된 사용자만 접근 가능
     */
    @PostMapping("/stop")
    public ResponseEntity<ApiResponse<RecordingResponseDto>> stopRecording (@AuthenticationPrincipal Long userId) {
        log.info("영상 녹화 중지 요청: userId={}", userId);
        
        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
        }
        
        RecordingResponseDto responseDto = videoService.stopRecording(userId);
        
        log.info("영상 녹화 중지 완료: userId={}", userId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, responseDto);
    }

    /**
     * 비디오 엔티티 생성
     * 
     * @param request 비디오 URL과 사용자 ID 정보
     * @return 엔티티 생성 성공 메시지
     * 
     * 처리 과정:
     * 1. 비디오 URL 목록과 사용자 ID 수신
     * 2. 비디오 엔티티 생성 및 저장
     * 
     * 보안: 인증된 사용자만 접근 가능
     */
    @PostMapping("/make-entity")
    public ResponseEntity<ApiResponse<String>> makeVideoEntity(@Valid @RequestBody MakeVideoEntityRequest request) {
        log.info("비디오 엔티티 생성 요청: userId={}, urlCount={}", request.getUserId(), request.getUrls().size());
        
        // DTO 검증 (추가 확인)
        if (request == null) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "요청 정보가 필요합니다.");
        }
        
        videoService.makeVideoEntity(request.getUrls(), request.getUserId());
        
        log.info("비디오 엔티티 생성 완료: userId={}", request.getUserId());
        return ApiResponse.toResponseEntity(SuccessCode.CREATED, "비디오 엔티티가 생성되었습니다.");
    }

    /**
     * 사용자별 영상 목록 조회
     * 
     * @param userId 인증된 사용자 ID
     * @return 해당 사용자의 영상 목록
     * 
     * 처리 과정:
     * 1. 사용자 ID로 영상 목록 조회
     * 2. 영상 정보 목록 반환
     * 
     * 보안: 인증된 사용자만 자신의 영상 조회 가능
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<VideoResponseDto>>> getAllVideos (@AuthenticationPrincipal Long userId) {
        log.info("사용자 영상 목록 조회: userId={}", userId);
        
        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
        }
        
        List<VideoResponseDto> responseDtoList = videoService.getAllVideosByUserId(userId);
        
        log.info("사용자 영상 목록 조회 완료: userId={}, count={}", userId, responseDtoList.size());
        return ApiResponse.toResponseEntity(SuccessCode.OK, responseDtoList);
    }

    /**
     * 관리자용 전체 영상 목록 조회
     * 
     * @param userId 인증된 관리자 ID
     * @return 전체 사용자의 영상 목록 (사용자별 그룹화)
     * 
     * 처리 과정:
     * 1. 전체 영상 목록 조회 (사용자별 그룹화)
     * 2. 관리자용 영상 목록 반환
     * 
     * 보안: ADMIN, MODERATOR 권한 필요
     * 권한: 관리자만 전체 영상 조회 가능
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/all/admin")
    public ResponseEntity<ApiResponse<List<VideoListResponseDto>>> getAllVideosForAdmin(@AuthenticationPrincipal Long userId) {
        log.info("관리자 영상 목록 조회: adminId={}", userId);
        
        // 관리자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 관리자 정보입니다.");
        }
        
        List<VideoListResponseDto> responseDtoList = videoService.getAllVideosGroupedByUser();
        
        log.info("관리자 영상 목록 조회 완료: adminId={}, userCount={}", userId, responseDtoList.size());
        return ApiResponse.toResponseEntity(SuccessCode.OK, responseDtoList);
    }

    /**
     * 영상 다운로드
     * 
     * @param userId 인증된 사용자 ID
     * @param filename 다운로드할 영상 파일명
     * @return 다운로드 응답 정보
     * 
     * 처리 과정:
     * 1. 파일명으로 영상 정보 조회
     * 2. 다운로드 URL 및 정보 반환
     * 
     * 보안: 인증된 사용자만 접근 가능
     */
    @GetMapping("/download/{filename}")
    public ResponseEntity<ApiResponse<DownloadResponseDto>> downloadVideo (@AuthenticationPrincipal Long userId,
                                                                           @PathVariable String filename){
        log.info("영상 다운로드 요청: userId={}, filename={}", userId, filename);
        
        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
        }
        
        // 파일명 검증
        if (filename == null || filename.trim().isEmpty()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "파일명을 입력해주세요.");
        }
        
        DownloadResponseDto responseDto = videoService.downloadVideo(filename);
        
        log.info("영상 다운로드 완료: userId={}, filename={}", userId, filename);
        return ApiResponse.toResponseEntity(SuccessCode.OK, responseDto);
    }
}
