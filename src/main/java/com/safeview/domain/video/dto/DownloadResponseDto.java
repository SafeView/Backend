package com.safeview.domain.video.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadResponseDto {
    private String url;
    private String filename;
    private String error;
}
