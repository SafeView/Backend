package com.safeview.global.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/*
 * 기본 엔티티 클래스
 * 
 * 모든 엔티티의 기본이 되는 추상 클래스
 * BaseTimeEntity를 상속받아 생성/수정 시간 정보를 포함
 */
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class BaseEntity extends BaseTimeEntity {
} 