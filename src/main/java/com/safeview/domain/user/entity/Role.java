package com.safeview.domain.user.entity;

import lombok.Getter;

/*
 * 사용자 역할 열거형
 * 
 * 사용자의 권한 레벨을 나타내는 열거형
 * USER: 일반 사용자, MODERATOR: 중간 관리자, ADMIN: 최고 관리자
 */
@Getter
public enum Role {
    USER,MODERATOR,ADMIN
}
