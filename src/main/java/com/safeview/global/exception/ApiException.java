package com.safeview.global.exception;

import com.safeview.global.response.ErrorCode;
import lombok.Getter;

/*
 * API 예외 클래스
 * 
 * 애플리케이션에서 발생하는 비즈니스 예외를 처리하는 클래스
 * ErrorCode와 추가 데이터를 포함하여 상세한 예외 정보 제공
 */
@Getter
public class ApiException extends RuntimeException {

    /*
     * 에러 코드
     */
    private final ErrorCode errorCode;
    
    /*
     * 추가 데이터
     */
    @Getter
    private final Object data;

    /*
     * 기본 생성자 (에러 코드만 포함)
     */
    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = null;
    }

    /*
     * 추가 데이터를 포함한 생성자
     */
    public ApiException(ErrorCode errorCode, Object data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = data;
    }

}