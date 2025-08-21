package com.safeview.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/*
 * 성공 코드 열거형
 * 
 * 애플리케이션에서 사용되는 모든 성공 코드를 정의
 * HTTP 상태 코드, 성공 코드, 성공 메시지를 포함
 */
@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    OK(HttpStatus.OK, "200", "요청에 성공했습니다."),
    CREATED(HttpStatus.CREATED, "201", "요청에 성공했으며, 리소스가 생성되었습니다."),
    ACCEPTED(HttpStatus.ACCEPTED, "202", "요청이 접수되었습니다."),
    RESOURCE_DELETED(HttpStatus.NO_CONTENT, "204", "요청에 성공했으나, 반환할 데이터가 없습니다.");

    /*
     * HTTP 상태 코드
     */
    private final HttpStatus httpStatus;
    
    /*
     * 성공 코드
     */
    private final String code;
    
    /*
     * 성공 메시지
     */
    private final String message;

    /*
     * ResponseEntity 형태의 응답 생성
     */
    public ResponseEntity<Void> toResponseEntity() {
        return ResponseEntity
                .status(this.httpStatus)
                .build();
    }
} 