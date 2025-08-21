package com.safeview.global.exception;

import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/*
 * 전역 예외 처리 클래스
 * 
 * 애플리케이션에서 발생하는 모든 예외를 처리하는 클래스
 * ApiException을 포함한 다양한 예외를 일관된 형태로 응답
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /*
     * ApiException 처리
     * 
     * 비즈니스 로직에서 발생하는 ApiException을 처리
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode, e.getData());
    }

    /*
     * 내부 예외 처리 메서드
     * 
     * ErrorCode와 데이터를 받아서 일관된 형태의 응답 생성
     */
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, Object data) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode, data));
    }

    /*
     * 에러 응답 생성
     * 
     * ErrorCode 정보를 바탕으로 ApiResponse 형태의 에러 응답 생성
     */
    private ApiResponse<Object> makeErrorResponse(ErrorCode errorCode, Object data) {
        return ApiResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), data);
    }
} 