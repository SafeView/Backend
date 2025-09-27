package com.safeview.domain.user.dto;

import com.safeview.domain.user.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/*
 * 사용자 회원가입 요청 DTO
 * 
 * 사용자 회원가입 시 클라이언트에서 전송하는 정보
 * 이메일, 비밀번호, 이름, 주소, 핸드폰 번호, 성별, 생년월일 포함
 */
@Getter
@Setter
public class UserSignUpRequestDto {

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @NotBlank(message = "주소는 필수 항목입니다.")
    private String address;

    @NotBlank(message = "전화번호는 필수 항목입니다.")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 전화번호 형식이 아닙니다. (예: 01012345678)")
    private String phone;

    @NotNull(message = "성별은 필수 항목입니다.")
    private Gender gender;

    @NotBlank(message = "생년월일은 필수 항목입니다.")
    private String birthday;
}