package com.safeview.domain.video.service;

import com.safeview.domain.video.dto.DownloadResponseDto;
import com.safeview.domain.video.dto.RecordingResponseDto;
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

import java.util.List;

import static com.safeview.global.response.ErrorCode.VIDEO_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService{

    private final VideoRepository videoRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

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

    public List<VideoResponseDto> getAllVideos(Long userId){
        List<Video> list = videoRepository.findAllByUserId(userId);

        return list.stream()
                .map(VideoResponseDto::from)
                .toList();
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
