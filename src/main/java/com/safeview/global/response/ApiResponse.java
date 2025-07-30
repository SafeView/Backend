package com.safeview.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.safeview.global.response.ErrorCode;
import com.safeview.global.response.SuccessCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"isSuccess", "code", "message", "data"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ApiResponse<T> onSuccess(T data) {
        return new ApiResponse<>(true, SuccessCode.OK.getCode(), SuccessCode.OK.getMessage(), data);
    }

    private static <T> ApiResponse<T> of(SuccessCode code, T data) {
        return new ApiResponse<>(true, code.getCode(), code.getMessage(), data);
    }

    public static <T> ApiResponse<T> onSuccessWithMessage(T data, String message) {
        return new ApiResponse<>(true, SuccessCode.OK.getCode(), message, data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> toResponseEntity(SuccessCode code, T data) {
        ApiResponse<T> body = ApiResponse.of(code, data);
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(body);
    }

    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }

    public static <T> ApiResponse<T> onFailure(ErrorCode errorCode, T data) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), data);
    }

    public static <T> ApiResponse<T> onFailure(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }
} 