package com.safeview.domain.video.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/*
 * 녹화 응답 DTO
 * 
 * AI 서버에서 영상 녹화 시작/중지 시 반환하는 정보
 * 파일명, S3 URL, 오류 정보를 포함
 */
@Getter
@Setter
public class RecordingResponseDto {

    @JsonProperty("filename")
    private String filename;
    @JsonProperty("s3_url")
    private String s3Url;
    @JsonProperty("error")
    private String error;
}
