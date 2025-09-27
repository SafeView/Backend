package com.safeview.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/*
 * API 응답 클래스
 * 
 * 모든 API 응답의 표준 형식을 정의하는 클래스
 * 성공/실패 여부, 코드, 메시지, 데이터를 포함한 일관된 응답 구조 제공
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"isSuccess", "code", "message", "data"})
public class ApiResponse<T> {

    /*
     * 요청 성공 여부
     */
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    
    /*
     * 응답 코드
     */
    private final String code;
    
    /*
     * 응답 메시지
     */
    private final String message;
    
    /*
     * 응답 데이터 (null인 경우에도 JSON에 포함)
     */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private T data;

    /*
     * 성공 응답 생성 (기본 메시지)
     */
    public static <T> ApiResponse<T> onSuccess(T data) {
        return new ApiResponse<>(true, SuccessCode.OK.getCode(), SuccessCode.OK.getMessage(), data);
    }

    /*
     * 성공 응답 생성 (내부 메서드)
     */
    private static <T> ApiResponse<T> of(SuccessCode code, T data) {
        return new ApiResponse<>(true, code.getCode(), code.getMessage(), data);
    }

    /*
     * 성공 응답 생성 (커스텀 메시지)
     */
    public static <T> ApiResponse<T> onSuccessWithMessage(T data, String message) {
        return new ApiResponse<>(true, SuccessCode.OK.getCode(), message, data);
    }

    /*
     * ResponseEntity 형태의 성공 응답 생성
     */
    public static <T> ResponseEntity<ApiResponse<T>> toResponseEntity(SuccessCode code, T data) {
        ApiResponse<T> body = ApiResponse.of(code, data);
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(body);
    }

    /*
     * 실패 응답 생성 (커스텀 정보)
     */
    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }

    /*
     * 실패 응답 생성 (ErrorCode + 데이터)
     */
    public static <T> ApiResponse<T> onFailure(ErrorCode errorCode, T data) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), data);
    }

    /*
     * 실패 응답 생성 (ErrorCode만)
     */
    public static <T> ApiResponse<T> onFailure(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }
} 