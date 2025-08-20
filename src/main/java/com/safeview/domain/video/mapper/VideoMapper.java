package com.safeview.domain.video.mapper;

import com.safeview.domain.video.dto.VideoResponseDto;
import com.safeview.domain.video.dto.VideoListResponseDto;
import com.safeview.domain.video.dto.RecordingResponseDto;
import com.safeview.domain.video.dto.DownloadResponseDto;
import com.safeview.domain.video.entity.Video;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 비디오 매퍼
 * 
 * Video 엔티티와 DTO 간의 변환을 담당
 * 영상 정보 변환, 녹화/다운로드 응답 생성 기능 제공
 */
@Component
public class VideoMapper {

    /*
     * Video 엔티티를 VideoResponseDto로 변환
     * 
     * 영상의 기본 정보를 응답 DTO로 변환
     */
    public VideoResponseDto toVideoResponseDto(Video video) {
        VideoResponseDto dto = new VideoResponseDto();
        dto.setId(video.getId());
        dto.setUserId(video.getUserId());
        dto.setFilename(video.getFilename());
        dto.setS3Url(video.getS3Url());
        return dto;
    }

    /*
     * Video 엔티티 리스트를 VideoResponseDto 리스트로 변환
     * 
     * 영상 목록을 응답 DTO 목록으로 변환
     */
    public List<VideoResponseDto> toVideoResponseDtoList(List<Video> videos) {
        return videos.stream()
                .map(this::toVideoResponseDto)
                .collect(Collectors.toList());
    }

    /*
     * Video 엔티티 리스트를 VideoListResponseDto로 변환
     * 
     * 사용자별 영상 목록을 관리자용 응답 DTO로 변환
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

    /*
     * 녹화 성공 응답 DTO 생성
     * 
     * 녹화 성공 시 AI 서버 응답을 위한 DTO 생성
     */
    public RecordingResponseDto toRecordingSuccessDto(String filename, String s3Url) {
        RecordingResponseDto dto = new RecordingResponseDto();
        dto.setFilename(filename);
        dto.setS3Url(s3Url);
        dto.setError(null);
        return dto;
    }

    /*
     * 녹화 실패 응답 DTO 생성
     * 
     * 녹화 실패 시 오류 정보를 포함한 DTO 생성
     */
    public RecordingResponseDto toRecordingErrorDto(String error) {
        RecordingResponseDto dto = new RecordingResponseDto();
        dto.setFilename(null);
        dto.setS3Url(null);
        dto.setError(error);
        return dto;
    }

    /*
     * 다운로드 성공 응답 DTO 생성
     * 
     * 다운로드 성공 시 다운로드 정보를 포함한 DTO 생성
     */
    public DownloadResponseDto toDownloadSuccessDto(String url, String filename) {
        DownloadResponseDto dto = new DownloadResponseDto();
        dto.setUrl(url);
        dto.setFilename(filename);
        dto.setError(null);
        return dto;
    }

    /*
     * 다운로드 실패 응답 DTO 생성
     * 
     * 다운로드 실패 시 오류 정보를 포함한 DTO 생성
     */
    public DownloadResponseDto toDownloadErrorDto(String error) {
        DownloadResponseDto dto = new DownloadResponseDto();
        dto.setUrl(null);
        dto.setFilename(null);
        dto.setError(error);
        return dto;
    }
}
