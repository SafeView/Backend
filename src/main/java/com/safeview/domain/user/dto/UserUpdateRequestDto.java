package com.safeview.domain.user.dto;

import com.safeview.domain.user.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/*
 * 사용자 정보 수정 요청 DTO
 * 
 * 사용자 정보 수정 시 클라이언트에서 전송하는 정보
 * 비밀번호, 이름, 주소, 전화번호, 성별, 생년월일 포함
 */
@Getter
@Setter
public class UserUpdateRequestDto {

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[a-z\\d@$!%*?&]+$",
        message = "비밀번호는 소문자 영문, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @NotBlank(message = "주소는 필수 항목입니다.")
    private String address;

    @NotBlank(message = "전화번호는 필수 항목입니다.")
    private String phone;

    @NotNull(message = "성별은 필수 항목입니다.")
    private Gender gender;

    @NotBlank(message = "생년월일은 필수 항목입니다.")
    private String birthday;
}
