package com.safeview.domain.user.entity;

import com.safeview.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/*
 * 사용자 엔티티
 * 
 * 시스템의 사용자 정보를 관리하는 엔티티
 * 이메일, 비밀번호, 개인정보, 역할 등을 포함
 */
@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    /*
     * 사용자 ID (기본키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /*
     * 이메일 주소 (고유값)
     */
    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    /*
     * 암호화된 비밀번호
     */
    @Column(name = "password", nullable = false)
    private String password;

    /*
     * 사용자 이름
     */
    @Column(name = "name", nullable = false)
    private String name;

    /*
     * 주소
     */
    @Column(name = "address", nullable = false)
    private String address;

    /*
     * 전화번호 (고유값)
     */
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    /*
     * 성별
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    /*
     * 생년월일
     */
    @Column(name = "birthday", nullable = false)
    private String birthday;

    /*
     * 사용자 역할 (USER, MODERATOR, ADMIN)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /*
     * 일반 사용자 생성 메서드 (기본 역할: USER)
     */
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

    /*
     * 관리자 생성 메서드 (역할: ADMIN)
     */
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

    /*
     * 중간 관리자 생성 메서드 (역할: MODERATOR)
     */
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

    /*
     * 사용자 역할 업데이트
     */
    public void updateRole(Role newRole) {
        this.role = newRole;
    }

    /*
     * 사용자 정보 업데이트
     */
    public void updateUserInfo(String password, String name, String address, String phone, Gender gender, String birthday) {
        this.password = password;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.gender = gender;
        this.birthday = birthday;
    }
}