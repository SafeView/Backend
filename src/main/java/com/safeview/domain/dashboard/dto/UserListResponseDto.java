package com.safeview.domain.dashboard.dto;

import com.safeview.domain.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 목록 응답 DTO
 * 
 * 관리자가 회원 목록을 조회할 때 사용하는 데이터 전송 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponseDto {
    
    /**
     * 사용자 ID
     */
    private Long userId;
    
    /**
     * 이메일
     */
    private String email;
    
    /**
     * 이름
     */
    private String name;
    
    /**
     * 전화번호
     */
    private String phone;
    
    /**
     * 주소
     */
    private String address;
    
    /**
     * 성별
     */
    private String gender;
    
    /**
     * 생년월일
     */
    private String birthday;
    
    /**
     * 역할
     */
    private Role role;
    
    /**
     * 가입일
     */
    private LocalDateTime createdAt;
    
    /**
     * 최근 수정일
     */
    private LocalDateTime updatedAt;
}
