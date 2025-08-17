package com.safeview.domain.video.service;

import com.safeview.domain.video.dto.DownloadResponseDto;
import com.safeview.domain.video.dto.RecordingResponseDto;
import com.safeview.domain.video.dto.VideoListResponseDto;
import com.safeview.domain.video.dto.VideoResponseDto;
import com.safeview.domain.video.entity.Video;
import com.safeview.domain.video.repository.VideoRepository;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.safeview.global.response.ErrorCode.VIDEO_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService{

    private final VideoRepository videoRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public void makeVideoEntity(List<String> urls, Long userId) {

        if(userId == null || urls == null || urls.isEmpty()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "유효하지 않은 요청입니다.");
        }

        for (String url : urls) {

            String filename = url.substring(url.lastIndexOf("/") + 1);

            Video video = Video.builder()
                    .userId(userId) // 임시로 userId를 1로 설정, 실제 사용 시 적절한 userId로 변경 필요
                    .filename(filename)
                    .s3Url(url)
                    .build();

            videoRepository.save(video);
        }
    }

    public RecordingResponseDto startRecording(){
        String url = aiServerUrl + "/start_recording";
        RecordingResponseDto response = restTemplate.postForObject(url, null, RecordingResponseDto.class);
        if(response == null){
            throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, "녹화 시작 실패");
        }

        if(response.getError() == null || !response.getError().equals("no error")){
            throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, response.getError() == null ? "알 수 없는 오류" : response.getError());
        }
        else return response;
    }


    public RecordingResponseDto stopRecording(Long userId) {
        String url = aiServerUrl + "/stop_recording";
        RecordingResponseDto response = restTemplate.postForObject(url, null, RecordingResponseDto.class);

        if(response == null){
            throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, "녹화 중단 및 S3 업로드 실패");
        }

        if(response.getError() == null || !response.getError().equals("no error")){
            throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, response.getError() == null ? "알 수 없는 오류" : response.getError());
        }

        Video video = Video.builder()
                .userId(userId)
                .filename(response.getFilename())
                .s3Url(response.getS3Url())
                .build();

        videoRepository.save(video);
        return response;
    }

    public List<VideoResponseDto> getAllVideosByUserId(Long userId){
        List<Video> list = videoRepository.findAllByUserId(userId);

        return list.stream()
                .map(VideoResponseDto::from)
                .toList();
    }

    public List<VideoListResponseDto> getAllVideosGroupedByUser() {
        List<Video> videos = videoRepository.findAll();
        Map<Long, Map<String, List<Video>>> grouped = new HashMap<>();

        for (Video video : videos) {
            Long userId = video.getUserId();
            String baseName = video.getFilename().replace("_raw", "");
            grouped
                    .computeIfAbsent(userId, k -> new HashMap<>())
                    .computeIfAbsent(baseName, k -> new ArrayList<>())
                    .add(video);
        }

        List<VideoListResponseDto> result = new ArrayList<>();
        for (Map.Entry<Long, Map<String, List<Video>>> userEntry : grouped.entrySet()) {
            for (Map.Entry<String, List<Video>> fileEntry : userEntry.getValue().entrySet()) {
                List<String> filenames = fileEntry.getValue().stream()
                        .map(Video::getFilename)
                        .toList();
                List<String> s3Urls = fileEntry.getValue().stream()
                        .map(Video::getS3Url)
                        .toList();
                result.add(new VideoListResponseDto(
                        userEntry.getKey(),
                        filenames,
                        s3Urls
                ));
            }
        }
        return result;
    }

    public DownloadResponseDto downloadVideo(String filename){
        if(videoRepository.findByFilename(filename) == null){
            throw new ApiException(VIDEO_NOT_FOUND, "비디오를 찾을 수 없습니다.");
        }
        String url = aiServerUrl + "/recordings/" + filename;
        DownloadResponseDto response = restTemplate.getForObject(url, DownloadResponseDto.class);

        if(response == null){
            throw new ApiException(VIDEO_NOT_FOUND, "다운로드 할 수 없습니다.");
        }

        if(response.getError() == null || !response.getError().equals("no error")){
            throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, response.getError() == null ? "알 수 없는 오류" : response.getError());
        }

        return response;
    }

}
