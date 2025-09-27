package com.safeview.global.exception;

import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * 전역 예외 처리 클래스
 * 
 * 애플리케이션에서 발생하는 모든 예외를 처리하는 클래스
 * ApiException을 포함한 다양한 예외를 일관된 형태로 응답
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

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
     * 잘못된 요청 본문 처리
     * 
     * JSON 파싱 오류나 잘못된 요청 형식을 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return handleExceptionInternal(ErrorCode.BAD_REQUEST, "잘못된 요청 형식입니다.");
    }

    /*
     * 유효성 검증 실패 처리
     * 
     * @Valid 어노테이션으로 인한 유효성 검증 실패를 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = "입력값이 올바르지 않습니다.";
        if (e.getBindingResult().hasFieldErrors()) {
            errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        }
        return handleExceptionInternal(ErrorCode.BAD_REQUEST, errorMessage);
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