package com.safeview.domain.video.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * 다운로드 응답 DTO
 * 
 * AI 서버에서 영상 다운로드 시 반환하는 정보
 * 다운로드 URL, 파일명, 오류 정보를 포함
 */
@Getter
@Setter
public class DownloadResponseDto {
    private String url;
    private String filename;
    private String error;
}
