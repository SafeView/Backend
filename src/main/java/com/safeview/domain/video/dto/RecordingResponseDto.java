package com.safeview.domain.video.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

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
