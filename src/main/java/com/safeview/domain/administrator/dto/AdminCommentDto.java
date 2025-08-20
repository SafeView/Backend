package com.safeview.domain.administrator.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 관리자 코멘트 DTO
 * 
 * 관리자가 권한 요청에 대한 코멘트를 작성할 때 사용하는 DTO
 */
@Getter
@NoArgsConstructor
public class AdminCommentDto {

    private String adminComment;
} 