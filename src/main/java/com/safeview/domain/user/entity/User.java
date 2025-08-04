package com.safeview.domain.user.entity;

import com.safeview.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "birthday", nullable = false)
    private String birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // 사용자 생성 메서드 (기본 역할: USER)
    public static User createUser(String email, String password, String name, String address, String phone, Gender gender, String birthday) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .address(address)
                .phone(phone)
                .gender(gender)
                .birthday(birthday)
                .role(Role.USER)
                .build();
    }

    // 관리자 생성 메서드
    public static User createAdmin(String email, String password, String name, String address, String phone, Gender gender, String birthday) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .address(address)
                .phone(phone)
                .gender(gender)
                .birthday(birthday)
                .role(Role.ADMIN)
                .build();
    }

    // 중간 관리자 생성 메서드
    public static User createModerator(String email, String password, String name, String address, String phone, Gender gender, String birthday) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .address(address)
                .phone(phone)
                .gender(gender)
                .birthday(birthday)
                .role(Role.MODERATOR)
                .build();
    }

    // 역할 업데이트 메서드
    public void updateRole(Role newRole) {
        this.role = newRole;
    }
}