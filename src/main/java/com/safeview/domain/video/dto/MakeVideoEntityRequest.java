package com.safeview.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/*
 * 비디오 엔티티 생성 요청 DTO
 * 
 * 비디오 엔티티 생성 시 클라이언트에서 전송하는 정보
 * 사용자 ID와 비디오 URL 목록을 포함
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MakeVideoEntityRequest {
    private Long userId;
    private List<String> urls;
}