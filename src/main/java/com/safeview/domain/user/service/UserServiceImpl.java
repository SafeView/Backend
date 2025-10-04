package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.*;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.mapper.UserMapper;
import com.safeview.domain.user.repository.UserRepository;
import com.safeview.domain.user.dto.UserInfoResponseDto;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스 구현체
 * 
 * 사용자 관련 비즈니스 로직을 담당합니다.
 * - 회원가입 처리 (이메일/전화번호 중복 확인, 비밀번호 암호화)
 * - 이메일 중복 확인
 * 
 * 보안: 비밀번호 암호화, 중복 데이터 검증
 * 감사: 회원가입 이력 관리
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationStore emailVerificationStore;

    /**
     * 회원가입 처리
     * 
     * @param requestDto 회원가입 요청 정보
     * @return 회원가입 성공 정보
     * 
     * 처리 과정:
     * 1. 이메일 중복 확인
     * 2. 전화번호 중복 확인
     * 3. User 엔티티 생성 (비밀번호 암호화 포함)
     * 4. 데이터베이스에 저장
     * 5. 회원가입 성공 응답 생성
     * 
     * 보안: 비밀번호 암호화, 중복 데이터 검증
     * 예외: 중복된 이메일, 중복된 전화번호
     */
    @Override
    @Transactional
    public UserSignUpResponseDto signUp(UserSignUpRequestDto requestDto) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 전화번호 중복 확인 (하이픈 제거 후 확인)
        String phoneWithoutHyphen = requestDto.getPhone().replaceAll("-", "");
        if (userRepository.existsByPhone(phoneWithoutHyphen)) {
            throw new ApiException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
        
        // User 엔티티 생성
        User user = userMapper.toEntity(requestDto);

        User savedUser = userRepository.save(user);

        return userMapper.toSignUpResponseDto(savedUser);
    }

    /**
     * 이메일 중복 확인
     * 
     * @param email 확인할 이메일 주소
     * @return 이메일 사용 가능 여부
     * 
     * 처리 과정:
     * 1. 데이터베이스에서 이메일 존재 여부 확인
     * 2. 중복된 경우 예외 발생
     * 3. 사용 가능한 경우 true 반환
     * 
     * 예외: 중복된 이메일
     */
    @Override
    public EmailCheckResponseDto checkEmail(String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        
        if (exists) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS, new EmailCheckResponseDto(false));
        }
        
        return new EmailCheckResponseDto(true);
    }

    /**
     * 사용자 정보 조회
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자 상세 정보
     * 
     * 처리 과정:
     * 1. 사용자 ID로 사용자 조회
     * 2. 사용자 정보를 DTO로 변환
     * 3. 사용자 정보 반환
     * 
     * 예외: 존재하지 않는 사용자
     */
    @Override
    public UserInfoResponseDto getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        
        return userMapper.toUserInfoResponseDto(user);
    }

    /**
     * 사용자 정보 수정
     * 
     * @param userId 수정할 사용자 ID
     * @param requestDto 수정할 사용자 정보
     * @return 수정된 사용자 정보
     * 
     * 처리 과정:
     * 1. 사용자 ID로 사용자 조회
     * 2. 전화번호 중복 확인 (다른 사용자와 중복되지 않는지)
     * 3. 비밀번호 암호화
     * 4. 사용자 정보 업데이트
     * 5. 수정된 사용자 정보 반환
     * 
     * 예외: 존재하지 않는 사용자, 중복된 전화번호
     */
    @Override
    @Transactional
    public UserInfoResponseDto updateUserInfo(Long userId, UserUpdateRequestDto requestDto) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 전화번호 중복 확인 (현재 사용자 제외)
        if (userRepository.existsByPhoneAndIdNot(requestDto.getPhone(), userId)) {
            throw new ApiException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 정보 업데이트
        user.updateUserInfo(
                encodedPassword,
                requestDto.getName(),
                requestDto.getAddress(),
                requestDto.getPhone(),
                requestDto.getGender(),
                requestDto.getBirthday()
        );

        User savedUser = userRepository.save(user);

        return userMapper.toUserInfoResponseDto(savedUser);
    }

    /**
     * 임시 비밀번호 발송
     * 
     * @param requestDto 임시 비밀번호 발송 요청 정보
     * @return 임시 비밀번호 발송 결과
     * 
     * 처리 과정:
     * 1. 이메일로 사용자 조회
     * 2. 임시 비밀번호 생성
     * 3. 사용자 비밀번호를 임시 비밀번호로 업데이트
     * 4. 이메일로 임시 비밀번호 발송
     * 5. 발송 완료 메시지 반환
     * 
     * 예외: 존재하지 않는 사용자
     */
    @Override
    @Transactional
    public TempPasswordResponseDto sendTempPassword(TempPasswordRequestDto requestDto) {
        // 사용자 조회
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 임시 비밀번호 생성 (8자리 영문+숫자)
        String tempPassword = generateTempPassword();

        // 사용자 비밀번호를 임시 비밀번호로 업데이트
        String encodedTempPassword = passwordEncoder.encode(tempPassword);
        user.updatePassword(encodedTempPassword);
        userRepository.save(user);

        // 이메일로 임시 비밀번호 발송
        emailService.sendTempPassword(requestDto.getEmail(), tempPassword);

        return new TempPasswordResponseDto("임시 비밀번호가 이메일로 발송되었습니다.");
    }

    /**
     * 이메일 인증번호 발송
     *
     * @param requestDto 이메일 인증번호 발송 요청
     * @return 발송 성공 메시지
     */
    @Override
    @Transactional
    public EmailVerificationResponseDto sendEmailVerificationCode(EmailVerificationRequestDto requestDto) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 6자리 인증번호 생성
        String verificationCode = generateVerificationCode();

        // 인증번호 저장 (5분 유효)
        emailVerificationStore.storeVerificationCode(requestDto.getEmail(), verificationCode);

        // 이메일 발송
        emailService.sendVerificationCode(requestDto.getEmail(), verificationCode);

        return new EmailVerificationResponseDto("인증번호가 이메일로 발송되었습니다.");
    }

    /**
     * 이메일 인증번호 검증
     *
     * @param requestDto 이메일 인증번호 검증 요청
     * @return 검증 성공 메시지
     */
    @Override
    public EmailVerificationResponseDto verifyEmailCode(EmailVerificationDto requestDto) {
        // 인증번호 검증
        boolean isValid = emailVerificationStore.verifyCode(requestDto.getEmail(), requestDto.getCode());
        
        if (!isValid) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "인증번호가 올바르지 않거나 만료되었습니다.");
        }

        return new EmailVerificationResponseDto("이메일 인증이 완료되었습니다.");
    }

    /**
     * 임시 비밀번호 생성
     * 
     * @return 8자리 임시 비밀번호 (영문 대소문자 + 숫자)
     */
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(8);
        
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }

    /**
     * 인증번호 생성
     * 
     * @return 6자리 인증번호
     */
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }
}
