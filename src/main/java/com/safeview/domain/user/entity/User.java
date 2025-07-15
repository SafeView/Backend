package com.safeview.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users") // "user"는 예약어일 수 있어 "users" 권장
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nickname;

    @Column(nullable = false)
    private String role; // 예: ROLE_USER, ROLE_ADMIN
}
