package com.safeview.domain.video.dto;

import com.safeview.domain.video.entity.Video;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoResponseDto {

    private Long id;
    private Long userId;
    private String filename;
    private String s3Url;

    public static VideoResponseDto from(Video video){
        VideoResponseDto dto = new VideoResponseDto();
        dto.id = video.getId();
        dto.userId = video.getUserId();
        dto.filename = video.getFilename();
        dto.s3Url = video.getS3Url();
        return dto;
    }
}
