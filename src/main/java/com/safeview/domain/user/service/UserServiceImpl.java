package com.safeview.domain.user.service;

import com.safeview.domain.user.dto.*;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.mapper.UserMapper;
import com.safeview.domain.user.repository.UserRepository;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스 구현체
 * 
 * 사용자 관련 비즈니스 로직을 담당합니다.
 * - 회원가입 처리 (이메일/전화번호 중복 확인, 비밀번호 암호화)
 * - 이메일 중복 확인
 * - 사용자 정보 조회
 * - 비밀번호 찾기 (이메일 인증)
 * 
 * 보안: 비밀번호 암호화, 중복 데이터 검증, 이메일 인증
 * 감사: 회원가입 이력 관리, 비밀번호 재설정 이력
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

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

        // 전화번호 중복 확인
        if (userRepository.existsByPhone(requestDto.getPhone())) {
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
     * 임시 비밀번호 발송
     * 
     * @param requestDto 이메일 주소
     * 
     * 처리 과정:
     * 1. 이메일 주소 검증
     * 2. 사용자 존재 확인
     * 3. 임시 비밀번호 생성
     * 4. 사용자 비밀번호를 임시 비밀번호로 변경
     * 5. 임시 비밀번호 이메일 발송
     * 
     * 보안: 이메일 주소 검증, 임시 비밀번호 암호화
     * 예외: 등록되지 않은 이메일, 이메일 발송 실패
     */
    @Override
    @Transactional
    public void sendTempPassword(TempPasswordRequestDto requestDto) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "등록되지 않은 이메일입니다."));

        // 임시 비밀번호 생성 (8자리 랜덤)
        String tempPassword = generateTempPassword();

        // 임시 비밀번호로 사용자 비밀번호 변경
        String encodedPassword = passwordEncoder.encode(tempPassword);
        user.updatePassword(encodedPassword);
        userRepository.save(user);

        // 임시 비밀번호 이메일 발송
        boolean emailSent = emailService.sendTempPassword(requestDto.getEmail(), tempPassword);
        if (!emailSent) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }


    /*
     * 임시 비밀번호 생성
     * 
     * @return 8자리 랜덤 임시 비밀번호
     */
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
}
