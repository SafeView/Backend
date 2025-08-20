package com.safeview.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/*
 * 비디오 목록 응답 DTO
 * 
 * 관리자가 전체 영상 목록을 조회할 때 사용하는 DTO
 * 사용자별로 그룹화된 영상 정보를 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoListResponseDto {

    private Long userId;
    private List<String> filenames;
    private List<String> s3Urls;


}
