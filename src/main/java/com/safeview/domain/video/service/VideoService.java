package com.safeview.domain.video.service;

import com.safeview.domain.video.dto.DownloadResponseDto;
import com.safeview.domain.video.dto.RecordingResponseDto;
import com.safeview.domain.video.dto.VideoResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VideoService {
    RecordingResponseDto startRecording ();
    RecordingResponseDto stopRecording(Long userId);
    List<VideoResponseDto> getAllVideos(Long userId);
    DownloadResponseDto downloadVideo(String filename);
}
