package com.safeview.global.exception;

import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

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
     * @Valid 검증 실패 처리
     * 
     * DTO 검증 실패 시 발생하는 MethodArgumentNotValidException을 처리
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
     * JSON 파싱 오류 처리
     * 
     * 잘못된 JSON 형식이나 타입 불일치 시 발생하는 HttpMessageNotReadableException을 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return handleExceptionInternal(ErrorCode.BAD_REQUEST, "잘못된 요청 형식입니다.");
    }

    /*
     * 잘못된 HTTP 메서드 처리
     * 
     * 지원하지 않는 HTTP 메서드로 요청 시 발생하는 HttpRequestMethodNotSupportedException을 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return handleExceptionInternal(ErrorCode.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다.");
    }

    /*
     * 잘못된 URL 처리
     * 
     * 존재하지 않는 URL로 요청 시 발생하는 NoHandlerFoundException을 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return handleExceptionInternal(ErrorCode.NOT_FOUND, "요청한 URL을 찾을 수 없습니다.");
    }

    /*
     * 필수 파라미터 누락 처리
     * 
     * 필수 쿼리 파라미터가 누락된 경우 발생하는 MissingServletRequestParameterException을 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return handleExceptionInternal(ErrorCode.BAD_REQUEST, "필수 파라미터가 누락되었습니다: " + e.getParameterName());
    }

    /*
     * 타입 불일치 처리
     * 
     * 파라미터 타입이 일치하지 않는 경우 발생하는 MethodArgumentTypeMismatchException을 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return handleExceptionInternal(ErrorCode.BAD_REQUEST, "잘못된 파라미터 타입입니다: " + e.getName());
    }

    /*
     * 일반 예외 처리
     * 
     * 예상치 못한 모든 예외를 처리하는 최종 핸들러
     * 로깅 후 일관된 에러 응답 반환
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        // 로깅 (실제 운영에서는 로그 레벨에 따라 조정)
        System.err.println("예상치 못한 예외 발생: " + e.getMessage());
        e.printStackTrace();
        
        return handleExceptionInternal(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
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