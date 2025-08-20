package com.safeview.domain.video.mapper;

import com.safeview.domain.video.dto.VideoResponseDto;
import com.safeview.domain.video.dto.VideoListResponseDto;
import com.safeview.domain.video.dto.RecordingResponseDto;
import com.safeview.domain.video.dto.DownloadResponseDto;
import com.safeview.domain.video.entity.Video;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VideoMapper {

    /**
     * Video 엔티티를 VideoResponseDto로 변환
     */
    public VideoResponseDto toVideoResponseDto(Video video) {
        VideoResponseDto dto = new VideoResponseDto();
        dto.setId(video.getId());
        dto.setUserId(video.getUserId());
        dto.setFilename(video.getFilename());
        dto.setS3Url(video.getS3Url());
        return dto;
    }

    /**
     * Video 엔티티 리스트를 VideoResponseDto 리스트로 변환
     */
    public List<VideoResponseDto> toVideoResponseDtoList(List<Video> videos) {
        return videos.stream()
                .map(this::toVideoResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Video 엔티티 리스트를 VideoListResponseDto로 변환
     */
    public VideoListResponseDto toVideoListResponseDto(Long userId, List<Video> videos) {
        List<String> filenames = videos.stream()
                .map(Video::getFilename)
                .collect(Collectors.toList());
        
        List<String> s3Urls = videos.stream()
                .map(Video::getS3Url)
                .collect(Collectors.toList());

        return new VideoListResponseDto(userId, filenames, s3Urls);
    }

    /**
     * 녹화 성공 응답 DTO 생성
     */
    public RecordingResponseDto toRecordingSuccessDto(String filename, String s3Url) {
        RecordingResponseDto dto = new RecordingResponseDto();
        dto.setFilename(filename);
        dto.setS3Url(s3Url);
        dto.setError(null);
        return dto;
    }

    /**
     * 녹화 실패 응답 DTO 생성
     */
    public RecordingResponseDto toRecordingErrorDto(String error) {
        RecordingResponseDto dto = new RecordingResponseDto();
        dto.setFilename(null);
        dto.setS3Url(null);
        dto.setError(error);
        return dto;
    }

    /**
     * 다운로드 성공 응답 DTO 생성
     */
    public DownloadResponseDto toDownloadSuccessDto(String url, String filename) {
        DownloadResponseDto dto = new DownloadResponseDto();
        dto.setUrl(url);
        dto.setFilename(filename);
        dto.setError(null);
        return dto;
    }

    /**
     * 다운로드 실패 응답 DTO 생성
     */
    public DownloadResponseDto toDownloadErrorDto(String error) {
        DownloadResponseDto dto = new DownloadResponseDto();
        dto.setUrl(null);
        dto.setFilename(null);
        dto.setError(error);
        return dto;
    }
}
