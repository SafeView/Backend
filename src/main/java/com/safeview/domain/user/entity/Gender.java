package com.safeview.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * 성별 열거형
 * 
 * 사용자의 성별을 나타내는 열거형
 */
@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE,
    FEMALE
}
