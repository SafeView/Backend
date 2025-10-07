package com.safeview.domain.video.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.safeview.domain.video.dto.DownloadResponseDto;
import com.safeview.domain.video.dto.RecordingResponseDto;
import com.safeview.domain.video.dto.VideoListResponseDto;
import com.safeview.domain.video.dto.VideoResponseDto;
import com.safeview.domain.video.entity.Video;
import com.safeview.domain.video.mapper.VideoMapper;
import com.safeview.domain.video.repository.VideoRepository;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * 비디오 서비스 구현체
 * 
 * CCTV 영상 관련 비즈니스 로직을 담당합니다.
 * - 영상 녹화 시작/중지 (AI 서버 연동)
 * - 비디오 엔티티 생성 및 관리
 * - 영상 목록 조회 (사용자별, 관리자용)
 * - 영상 다운로드 (AI 서버 연동)
 * 
 * 보안: 사용자별 영상 접근 권한 검증
 * 감사: 영상 생성 및 조회 이력 관리
 * 외부 연동: AI 서버와 REST API 통신
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class VideoServiceImpl implements VideoService{

    private final VideoRepository videoRepository;
    private final RestTemplate restTemplate;
    private final VideoMapper videoMapper;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    /**
     * 비디오 엔티티 생성
     * 
     * @param urls 비디오 URL 목록
     * @param userId 사용자 ID
     * 
     * 처리 과정:
     * 1. 입력값 검증 (userId, urls)
     * 2. URL에서 파일명 추출
     * 3. Video 엔티티 생성 및 저장
     * 
     * 보안: 입력값 검증
     * 예외: 유효하지 않은 요청
     */
    @Override
    @Transactional
    public void makeVideoEntity(List<String> urls, Long userId) {
        log.info("비디오 엔티티 생성 시작: userId={}, urlCount={}", userId, urls.size());

        if(userId == null || urls == null || urls.isEmpty()) {
            log.warn("유효하지 않은 요청: userId={}, urls={}", userId, urls);
            throw new ApiException(ErrorCode.BAD_REQUEST, "유효하지 않은 요청입니다.");
        }

        try {
            for (String url : urls) {
                String filename = url.substring(url.lastIndexOf("/") + 1);

                Video video = Video.builder()
                        .userId(userId)
                        .filename(filename)
                        .s3Url(url)
                        .build();

                videoRepository.save(video);
                log.debug("비디오 엔티티 저장: filename={}, userId={}", filename, userId);
            }
            
            log.info("비디오 엔티티 생성 완료: userId={}, count={}", userId, urls.size());
        } catch (Exception e) {
            log.error("비디오 엔티티 생성 중 오류: userId={}", userId, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "비디오 엔티티 생성 중 오류가 발생했습니다.");
        }
    }

    /**
     * 영상 녹화 시작
     * 
     * @return 녹화 시작 응답 정보
     * 
     * 처리 과정:
     * 1. AI 서버에 녹화 시작 요청
     * 2. 응답 검증 및 반환
     * 
     * 외부 연동: AI 서버 REST API 호출
     * 예외: 녹화 시작 실패, AI 서버 오류
     */
    @Override
    @Transactional
    public RecordingResponseDto startRecording(){
        log.info("영상 녹화 시작 요청");
        
        try {
            String url = aiServerUrl + "/start_recording";
            RecordingResponseDto response = restTemplate.postForObject(url, null, RecordingResponseDto.class);
            
            if(response == null){
                log.error("AI 서버 응답 없음: 녹화 시작 실패");
                throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, "녹화 시작 실패");
            }

            if(response.getError() == null || !response.getError().equals("no error")){
                log.error("AI 서버 오류: {}", response.getError());
                throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, response.getError() == null ? "알 수 없는 오류" : response.getError());
            }
            
            log.info("영상 녹화 시작 완료");
            return response;
        } catch (ApiException e) {
            log.error("영상 녹화 시작 실패: error={}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("영상 녹화 시작 중 예상치 못한 오류", e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "영상 녹화 시작 중 오류가 발생했습니다.");
        }
    }


    /**
     * 영상 녹화 중지
     * 
     * @param userId 사용자 ID
     * @return 녹화 중지 응답 정보
     * 
     * 처리 과정:
     * 1. AI 서버에 녹화 중지 요청
     * 2. 응답 검증
     * 3. Video 엔티티 생성 및 저장
     * 
     * 외부 연동: AI 서버 REST API 호출
     * 예외: 녹화 중지 실패, AI 서버 오류
     */
    @Override
    @Transactional
    public RecordingResponseDto stopRecording(Long userId) {
        log.info("영상 녹화 중지 요청: userId={}", userId);
        
        try {
            String url = aiServerUrl + "/stop_recording";
            RecordingResponseDto response = restTemplate.postForObject(url, null, RecordingResponseDto.class);

            if(response == null){
                log.error("AI 서버 응답 없음: 녹화 중지 실패");
                throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, "녹화 중단 및 S3 업로드 실패");
            }

            if(response.getError() == null || !response.getError().equals("no error")){
                log.error("AI 서버 오류: {}", response.getError());
                throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, response.getError() == null ? "알 수 없는 오류" : response.getError());
            }

            Video video = Video.builder()
                    .userId(userId)
                    .filename(response.getFilename())
                    .s3Url(response.getS3Url())
                    .build();

            videoRepository.save(video);
            log.info("영상 녹화 중지 완료: userId={}, filename={}", userId, response.getFilename());
            return response;
        } catch (ApiException e) {
            log.error("영상 녹화 중지 실패: userId={}, error={}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("영상 녹화 중지 중 예상치 못한 오류: userId={}", userId, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "영상 녹화 중지 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자별 영상 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자의 영상 목록
     * 
     * 처리 과정:
     * 1. 사용자 ID로 영상 목록 조회
     * 2. DTO 변환하여 반환
     * 
     * 보안: 사용자별 영상 접근 권한 검증
     */
    @Override
    public List<VideoResponseDto> getAllVideosByUserId(Long userId){
        log.info("사용자 영상 목록 조회: userId={}", userId);
        
        try {
            List<Video> list = videoRepository.findAllByUserId(userId);
            
            log.info("사용자 영상 목록 조회 완료: userId={}, count={}", userId, list.size());
            return videoMapper.toVideoResponseDtoList(list);
        } catch (Exception e) {
            log.error("사용자 영상 목록 조회 중 오류: userId={}", userId, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "영상 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 관리자용 전체 영상 목록 조회 (사용자별 그룹화)
     * 
     * @return 전체 사용자의 영상 목록 (사용자별 그룹화)
     * 
     * 처리 과정:
     * 1. 전체 영상 목록 조회
     * 2. 사용자별, 파일별로 그룹화
     * 3. 관리자용 DTO로 변환하여 반환
     * 
     * 보안: 관리자 권한 필요
     */
    @Override
    public List<VideoListResponseDto> getAllVideosGroupedByUser() {
        log.info("관리자용 전체 영상 목록 조회 시작");
        
        try {
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
                    result.add(videoMapper.toVideoListResponseDto(
                            userEntry.getKey(),
                            fileEntry.getValue()
                    ));
                }
            }
            
            log.info("관리자용 전체 영상 목록 조회 완료: userCount={}, totalCount={}", grouped.size(), result.size());
            return result;
        } catch (Exception e) {
            log.error("관리자용 전체 영상 목록 조회 중 오류", e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "영상 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 영상 다운로드
     * 
     * @param filename 다운로드할 영상 파일명
     * @return 다운로드 응답 정보
     * 
     * 처리 과정:
     * 1. 파일명으로 영상 존재 여부 확인
     * 2. AI 서버에 다운로드 요청
     * 3. 응답 검증 및 반환
     * 
     * 외부 연동: AI 서버 REST API 호출
     * 예외: 영상 없음, 다운로드 실패, AI 서버 오류
     */
    @Override
    public DownloadResponseDto downloadVideo(String filename){
        log.info("영상 다운로드 요청: filename={}", filename);
        
        try {
            if(videoRepository.findByFilename(filename) == null){
                log.warn("영상을 찾을 수 없음: filename={}", filename);
                throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, "비디오를 찾을 수 없습니다.");
            }
            
            String url = aiServerUrl + "/recordings/" + filename;
            DownloadResponseDto response = restTemplate.getForObject(url, DownloadResponseDto.class);

            if(response == null){
                log.error("AI 서버 응답 없음: 다운로드 실패, filename={}", filename);
                throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, "다운로드 할 수 없습니다.");
            }

            if(response.getError() == null || !response.getError().equals("no error")){
                log.error("AI 서버 오류: filename={}, error={}", filename, response.getError());
                throw new ApiException(ErrorCode.VIDEO_NOT_FOUND, response.getError() == null ? "알 수 없는 오류" : response.getError());
            }

            log.info("영상 다운로드 완료: filename={}", filename);
            return response;
        } catch (ApiException e) {
            log.error("영상 다운로드 실패: filename={}, error={}", filename, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("영상 다운로드 중 예상치 못한 오류: filename={}", filename, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "영상 다운로드 중 오류가 발생했습니다.");
        }
    }

    /**
     *
     * @param filename 스트리밍할 영상 파일명
     * @param rangeHeader HTTP Range 헤더 (부분 스트리밍 요청 시 시작~끝 바이트 범위)
     * @return 요청한 구간의 영상 데이터와 함께 반환되는 스트리밍 응답(ResponseEntity)
     *
     * 처리 과정:
     * 1. 요청받은 파일명을 기반으로 S3 객체 키 생성 (recordings/{filename})
     * 2. Amazon S3에서 해당 객체 메타데이터 및 파일 크기 조회
     * 3. Range 헤더가 존재할 경우, 시작 및 종료 바이트 범위 계산
     * 4. S3ObjectInputStream에서 해당 구간만큼 데이터를 읽어 byte 배열 생성
     * 5. HTTP 헤더(Content-Type, Content-Range, Content-Length 등) 설정
     * 6. 206 Partial Content 상태로 응답 반환
     *
     * 외부 연동:
     * 프론트엔드의 <video> 태그 또는 axios/fetch 요청에서
     * Range 기반 스트리밍 재생 요청을 보낼 때 호출됨
     *
     * 예외:
     * - 영상 파일이 존재하지 않거나 S3 접근 실패 시 예외 발생
     * - I/O 처리 중 오류 발생 시 RuntimeException으로 래핑되어 전파
     */
    @Override
    public ResponseEntity<byte[]> streamVideo(String filename, String rangeHeader) {
        String key = "recordings/" + filename;

        S3Object s3Object = amazonS3.getObject(bucketName, key);
        long fileSize = s3Object.getObjectMetadata().getContentLength();

        long rangeStart = 0;
        long rangeEnd = fileSize - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            rangeStart = Long.parseLong(ranges[0]);
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                rangeEnd = Long.parseLong(ranges[1]);
            }
        }

        long chunkSize = rangeEnd - rangeStart + 1;

        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            inputStream.skip(rangeStart);
            byte[] data = inputStream.readNBytes((int) chunkSize);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
            headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));
            headers.add(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileSize));

            return new ResponseEntity<>(data, headers, HttpStatus.PARTIAL_CONTENT);

        } catch (IOException e) {
            throw new RuntimeException("Failed to stream video", e);
        }
    }
}
