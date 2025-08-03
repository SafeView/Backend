package com.safeview.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "403", "금지된 요청입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER4091", "이미 존재하는 이메일입니다."),
    PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER4092", "이미 존재하는 전화번호입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER4093", "이미 사용 중인 닉네임입니다."),
    VIDEO_NOT_FOUND(HttpStatus.BAD_REQUEST, "4001", "비디오를 찾을 수 없습니다."),

    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED,"AUTH_001", "유효하지 않은 JWT 토큰입니다."),
    MISSING_JWT_TOKEN(HttpStatus.UNAUTHORIZED,"AUTH_002", "쿠키에 accessToken이 존재하지 않습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "USER_002", "비밀번호가 일치하지 않습니다."),

    INVALID_API_KEY(HttpStatus.UNAUTHORIZED, "AUTH_003", "유효하지 않은 API Key입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
} 