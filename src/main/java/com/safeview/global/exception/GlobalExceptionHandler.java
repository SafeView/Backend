package com.safeview.global.exception;

import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode, e.getData());
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, Object data) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode, data));
    }

    private ApiResponse<Object> makeErrorResponse(ErrorCode errorCode, Object data) {
        return ApiResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), data);
    }
} 