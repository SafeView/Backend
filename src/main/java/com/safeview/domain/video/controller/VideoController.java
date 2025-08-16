package com.safeview.domain.video.controller;


import com.safeview.domain.video.dto.*;
import com.safeview.domain.video.service.VideoService;
import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<RecordingResponseDto>> startRecording (@AuthenticationPrincipal Long userId) {
        RecordingResponseDto responseDto = videoService.startRecording();
        return ApiResponse.toResponseEntity(SuccessCode.OK, responseDto);
    }

    @PostMapping("/stop")
    public ResponseEntity<ApiResponse<RecordingResponseDto>> stopRecording (@AuthenticationPrincipal Long userId) {
        RecordingResponseDto responseDto = videoService.stopRecording(userId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, responseDto);
    }

    @PostMapping("/make-entity")
    public ResponseEntity<ApiResponse<String>> makeVideoEntity(@RequestBody MakeVideoEntityRequest request) {
        videoService.makeVideoEntity(request.getUrls(), request.getUserId());
        return ApiResponse.toResponseEntity(SuccessCode.CREATED, "비디오 엔티티가 생성되었습니다.");
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<VideoResponseDto>>> getAllVideos (@AuthenticationPrincipal Long userId) {
        List<VideoResponseDto> responseDtoList = videoService.getAllVideosByUserId(userId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, responseDtoList);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/all/admin")
    public ResponseEntity<ApiResponse<List<VideoListResponseDto>>> getAllVideosForAdmin(@AuthenticationPrincipal Long userId) {
        List<VideoListResponseDto> responseDtoList = videoService.getAllVideosGroupedByUser();
        return ApiResponse.toResponseEntity(SuccessCode.OK, responseDtoList);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<ApiResponse<DownloadResponseDto>> downloadVideo (@AuthenticationPrincipal Long userId,
                                                                           @PathVariable String filename){
        DownloadResponseDto responseDto = videoService.downloadVideo(filename);
        return ApiResponse.toResponseEntity(SuccessCode.OK, responseDto);
    }
}
