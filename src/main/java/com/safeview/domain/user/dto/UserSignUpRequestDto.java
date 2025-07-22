package com.safeview.domain.user.dto;

import com.safeview.domain.user.entity.Gender;
import com.safeview.domain.user.entity.Role;
import com.safeview.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

    @NotNull(message = "역할은 필수 항목입니다.")
    private Role role;

    @NotBlank(message = "주소는 필수 항목입니다.")
    private String address;

    @NotBlank(message = "전화번호는 필수 항목입니다.")
    private String phone;

    @NotNull(message = "성별은 필수 항목입니다.")
    private Gender gender;

    @NotBlank(message = "생년월일은 필수 항목입니다.")
    private String birthday;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(this.email)
                .password(encodedPassword) // 암호화된 비밀번호를 사용
                .name(this.name)
                .role(this.role)
                .address(this.address)
                .phone(this.phone)
                .gender(this.gender)
                .birthday(this.birthday)
                .build();
    }
}