package com.safeview.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoListResponseDto {

    private Long userId;
    private List<String> filenames;
    private List<String> s3Urls;


}
