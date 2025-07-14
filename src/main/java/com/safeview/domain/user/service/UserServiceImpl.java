package com.safeview.domain.user.service;
import com.safeview.domain.user.dto.EmailCheckResponseDto;

import com.safeview.domain.user.dto.UserSignUpRequestDto;
import com.safeview.domain.user.dto.UserSignUpResponseDto;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.repository.UserRepository;
import com.safeview.global.resopnse.ApiException;
import com.safeview.global.resopnse.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    /*
    * 회원가입
     */
    private final UserRepository userRepository;
    // private final PasswordEncoder passwordEncoder; // Spring Security 추가 후 주석 해제

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

        // 비밀번호 암호화
        String encodedPassword = requestDto.getPassword(); // 실제로는 암호화 필요
        // String encodedPassword = passwordEncoder.encode(requestDto.getPassword()); // Spring Security 추가 후 이 코드를 사용

        User user = requestDto.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);

        return UserSignUpResponseDto.from(savedUser);
    }

    /*
    * 이메일 중복 체크
     */
    @Override
    public EmailCheckResponseDto checkEmail(String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        String message = exists ? "이미 존재하는 이메일입니다." : "사용 가능한 이메일입니다.";
        if (exists) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS, new EmailCheckResponseDto(true, message));
        }
        return new EmailCheckResponseDto(false, message);
    }
}
