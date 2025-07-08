package com.safeview.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    OK(HttpStatus.OK, "200", "요청에 성공했습니다."),
    CREATED(HttpStatus.CREATED, "201", "요청에 성공했으며, 리소스가 생성되었습니다."),
    ACCEPTED(HttpStatus.ACCEPTED, "202", "요청이 접수되었습니다."),
    RESOURCE_DELETED(HttpStatus.NO_CONTENT, "204", "요청에 성공했으나, 반환할 데이터가 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public ResponseEntity<Void> toResponseEntity() {
        return ResponseEntity
                .status(this.httpStatus)
                .build();
    }
} 