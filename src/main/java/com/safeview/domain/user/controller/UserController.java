package com.safeview.domain.user.controller;

import com.safeview.domain.user.dto.EmailCheckResponseDto;
import com.safeview.domain.user.dto.UserSignUpRequestDto;
import com.safeview.domain.user.dto.UserSignUpResponseDto;
import com.safeview.domain.user.dto.UserUpdateRequestDto;
import com.safeview.domain.user.dto.TempPasswordRequestDto;
import com.safeview.domain.user.dto.TempPasswordResponseDto;
import com.safeview.domain.user.dto.EmailVerificationRequestDto;
import com.safeview.domain.user.dto.EmailVerificationDto;
import com.safeview.domain.user.dto.EmailVerificationResponseDto;
import com.safeview.domain.user.service.UserService;
import com.safeview.domain.user.dto.UserInfoResponseDto;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ApiResponse;
import com.safeview.global.response.ErrorCode;
import com.safeview.global.response.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 컨트롤러
 * 
 * 사용자 관련 API를 제공합니다.
 * - 회원가입
 * - 이메일 중복 확인
 * - 사용자 정보 조회
 * - 사용자 정보 수정
 * - 임시 비밀번호 발송
 * 
 * 보안: 입력값 검증, 이메일 형식 검증, JWT 토큰 기반 인증
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {


    private final UserService userService;

    /**
     * 회원가입
     * 
     * @param requestDto 회원가입 요청 정보 (이메일, 비밀번호, 이름, 주소, 전화번호, 성별, 생년월일)
     * @return 회원가입 성공 정보
     * 
     * 처리 과정:
     * 1. 입력값 검증 (@Valid)
     * 2. 이메일 중복 확인
     * 3. 비밀번호 암호화
     * 4. 사용자 정보 저장
     * 5. 회원가입 성공 응답
     * 
     * 보안: 비밀번호 암호화, 입력값 검증
     * 예외: 중복된 이메일, 유효하지 않은 입력값
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignUpResponseDto>> signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) {
        log.info("회원가입 요청: email={}", requestDto.getEmail());
        
        UserSignUpResponseDto responseDto = userService.signUp(requestDto);
        
        log.info("회원가입 성공: email={}", requestDto.getEmail());
        return ApiResponse.toResponseEntity(SuccessCode.CREATED, responseDto);
    }

    /**
     * 이메일 중복 확인
     * 
     * @param email 확인할 이메일 주소
     * @return 이메일 사용 가능 여부
     * 
     * 처리 과정:
     * 1. 이메일 형식 검증
     * 2. 데이터베이스에서 이메일 중복 확인
     * 3. 사용 가능 여부 반환
     * 
     * 보안: 이메일 형식 검증
     * 예외: 유효하지 않은 이메일 형식
     */
    @GetMapping("/check-email")
    public ApiResponse<EmailCheckResponseDto> checkEmail(@RequestParam String email) {
        log.info("이메일 중복 확인 요청: email={}", email);
        
        EmailCheckResponseDto response = userService.checkEmail(email);
        
        log.info("이메일 중복 확인 완료: email={}, available={}", email, response.isAvailable());
        return ApiResponse.onSuccessWithMessage(response, "사용가능한 이메일입니다.");
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     * 
     * @param userId 인증된 사용자 ID
     * @return 현재 사용자의 상세 정보
     * 
     * 처리 과정:
     * 1. JWT 필터에서 인증된 사용자 ID 자동 주입
     * 2. 사용자 정보 조회 및 반환
     * 
     * 보안: JWT 필터에서 이미 인증 검증 완료
     * 예외: 존재하지 않는 사용자
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getMyInfo(@AuthenticationPrincipal Long userId) {
        log.info("사용자 정보 조회 요청: userId={}", userId);
        
        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
        }
        
        UserInfoResponseDto userInfo = userService.getUserInfoById(userId);

        log.info("사용자 정보 조회 완료: userId={}", userId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, userInfo);
    }

    /**
     * 사용자 정보 수정
     * 
     * @param userId 인증된 사용자 ID
     * @param requestDto 수정할 사용자 정보
     * @return 수정된 사용자 정보
     * 
     * 처리 과정:
     * 1. JWT 필터에서 인증된 사용자 ID 자동 주입
     * 2. 전화번호 중복 확인 (다른 사용자와 중복되지 않는지)
     * 3. 비밀번호 암호화
     * 4. 사용자 정보 업데이트
     * 5. 수정된 사용자 정보 반환
     * 
     * 보안: JWT 필터에서 이미 인증 검증 완료, 전화번호 중복 검증
     * 예외: 존재하지 않는 사용자, 중복된 전화번호
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> updateMyInfo(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserUpdateRequestDto requestDto) {
        log.info("사용자 정보 수정 요청: userId={}", userId);
        
        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
        }
        
        UserInfoResponseDto updatedUserInfo = userService.updateUserInfo(userId, requestDto);
        
        log.info("사용자 정보 수정 완료: userId={}", userId);
        return ApiResponse.toResponseEntity(SuccessCode.OK, updatedUserInfo);
    }

    /**
     * 임시 비밀번호 발송
     * 
     * @param requestDto 임시 비밀번호 발송 요청 정보
     * @return 임시 비밀번호 발송 결과
     */
    @PostMapping("/temp-password")
    public ResponseEntity<ApiResponse<TempPasswordResponseDto>> sendTempPassword(
            @Valid @RequestBody TempPasswordRequestDto requestDto) {
        log.info("임시 비밀번호 발송 요청: email={}", requestDto.getEmail());

        TempPasswordResponseDto response = userService.sendTempPassword(requestDto);

        log.info("임시 비밀번호 발송 완료: email={}", requestDto.getEmail());
        return ApiResponse.toResponseEntity(SuccessCode.OK, response);
    }

    /**
     * 이메일 인증번호 발송
     * 
     * @param requestDto 이메일 인증번호 발송 요청 정보
     * @return 이메일 인증번호 발송 결과
     */
    @PostMapping("/email-verification/send")
    public ResponseEntity<ApiResponse<EmailVerificationResponseDto>> sendEmailVerificationCode(
            @Valid @RequestBody EmailVerificationRequestDto requestDto) {
        log.info("이메일 인증번호 발송 요청: email={}", requestDto.getEmail());

        EmailVerificationResponseDto response = userService.sendEmailVerificationCode(requestDto);

        log.info("이메일 인증번호 발송 완료: email={}", requestDto.getEmail());
        return ApiResponse.toResponseEntity(SuccessCode.OK, response);
    }

    /**
     * 이메일 인증번호 검증
     * 
     * @param requestDto 이메일 인증번호 검증 요청 정보
     * @return 이메일 인증번호 검증 결과
     */
    @PostMapping("/email-verification/verify")
    public ResponseEntity<ApiResponse<EmailVerificationResponseDto>> verifyEmailCode(
            @Valid @RequestBody EmailVerificationDto requestDto) {
        log.info("이메일 인증번호 검증 요청: email={}", requestDto.getEmail());

        EmailVerificationResponseDto response = userService.verifyEmailCode(requestDto);

        log.info("이메일 인증번호 검증 완료: email={}", requestDto.getEmail());
        return ApiResponse.toResponseEntity(SuccessCode.OK, response);
    }
}
