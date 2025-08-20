package com.safeview.domain.user.mapper;

import com.safeview.domain.user.dto.UserSignUpRequestDto;
import com.safeview.domain.user.dto.UserSignUpResponseDto;
import com.safeview.domain.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/*
 * 사용자 매퍼
 * 
 * User 엔티티와 DTO 간의 변환을 담당
 * 비밀번호 암호화를 포함한 변환 로직 처리
 */
@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * UserSignUpRequestDto를 User 엔티티로 변환
     * 
     * 비밀번호 암호화를 포함한 변환 처리
     */
    public User toEntity(UserSignUpRequestDto requestDto) {
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        
        return User.createUser(
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getName(),
                requestDto.getAddress(),
                requestDto.getPhone(),
                requestDto.getGender(),
                requestDto.getBirthday()
        );
    }

    /*
     * User 엔티티를 UserSignUpResponseDto로 변환
     * 
     * 회원가입 성공 시 사용자 ID만 반환
     */
    public UserSignUpResponseDto toSignUpResponseDto(User user) {
        return new UserSignUpResponseDto(user.getId());
    }
}
