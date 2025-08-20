package com.safeview.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 비디오 응답 DTO
 * 
 * 사용자별 영상 목록 조회 시 클라이언트에게 반환하는 정보
 * 영상의 기본 정보를 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponseDto {

    private Long id;
    private Long userId;
    private String filename;
    private String s3Url;
}
