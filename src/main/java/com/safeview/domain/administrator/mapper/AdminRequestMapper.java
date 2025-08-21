package com.safeview.domain.administrator.mapper;

import com.safeview.domain.administrator.dto.AdminRequestCreateDto;
import com.safeview.domain.administrator.dto.AdminRequestResponseDto;
import com.safeview.domain.administrator.dto.AdminRequestSummaryDto;
import com.safeview.domain.administrator.dto.AdminRequestSummaryForAdminDto;
import com.safeview.domain.administrator.entity.AdminRequest;
import org.springframework.stereotype.Component;

@Component
public class AdminRequestMapper {

    /*
     * AdminRequestCreateDto를 AdminRequest 엔티티로 변환
     */
    public AdminRequest toEntity(Long userId, AdminRequestCreateDto createDto) {
        return AdminRequest.builder()
                .userId(userId)
                .title(createDto.getTitle())
                .description(createDto.getDescription())
                .build();
    }

    /*
     * AdminRequest 엔티티를 AdminRequestResponseDto로 변환
     */
    public AdminRequestResponseDto toResponseDto(AdminRequest adminRequest) {
        AdminRequestResponseDto dto = new AdminRequestResponseDto();
        dto.setId(adminRequest.getId());
        dto.setUserId(adminRequest.getUserId());
        dto.setTitle(adminRequest.getTitle());
        dto.setDescription(adminRequest.getDescription());
        dto.setStatus(adminRequest.getStatus());
        dto.setAdminComment(adminRequest.getAdminComment());
        dto.setProcessedAt(adminRequest.getProcessedAt());
        dto.setProcessedBy(adminRequest.getProcessedBy());
        dto.setCreatedAt(adminRequest.getCreatedAt());
        dto.setUpdatedAt(adminRequest.getUpdatedAt());
        return dto;
    }

    /*
     * AdminRequest 엔티티를 AdminRequestSummaryDto로 변환 (목록 조회용)
     */
    public AdminRequestSummaryDto toSummaryDto(AdminRequest adminRequest) {
        AdminRequestSummaryDto dto = new AdminRequestSummaryDto();
        dto.setId(adminRequest.getId());
        dto.setTitle(adminRequest.getTitle());
        dto.setStatus(adminRequest.getStatus());
        dto.setCreatedAt(adminRequest.getCreatedAt());
        return dto;
    }

    /*
     * AdminRequest 엔티티를 AdminRequestSummaryForAdminDto로 변환 (관리자 목록 조회용)
     */
    public AdminRequestSummaryForAdminDto toSummaryForAdminDto(AdminRequest adminRequest) {
        AdminRequestSummaryForAdminDto dto = new AdminRequestSummaryForAdminDto();
        dto.setId(adminRequest.getId());
        dto.setUserId(adminRequest.getUserId());
        dto.setTitle(adminRequest.getTitle());
        dto.setStatus(adminRequest.getStatus());
        dto.setCreatedAt(adminRequest.getCreatedAt());
        return dto;
    }
} 