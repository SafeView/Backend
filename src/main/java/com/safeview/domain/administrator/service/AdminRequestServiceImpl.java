package com.safeview.domain.administrator.service;

import com.safeview.domain.administrator.dto.AdminRequestCreateDto;
import com.safeview.domain.administrator.dto.AdminRequestResponseDto;
import com.safeview.domain.administrator.dto.AdminRequestSummaryDto;
import com.safeview.domain.administrator.entity.AdminRequest;
import com.safeview.domain.administrator.mapper.AdminRequestMapper;
import com.safeview.domain.administrator.repository.AdminRequestRepository;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRequestServiceImpl implements AdminRequestService {

    private final AdminRequestRepository adminRequestRepository;
    private final AdminRequestMapper adminRequestMapper;

    @Override
    @Transactional
    public AdminRequestResponseDto createAdminRequest(Long userId, AdminRequestCreateDto createDto) {
        AdminRequest adminRequest = adminRequestMapper.toEntity(userId, createDto);
        AdminRequest savedRequest = adminRequestRepository.save(adminRequest);
        return adminRequestMapper.toResponseDto(savedRequest);
    }

    @Override
    public List<AdminRequestSummaryDto> getUserRequests(Long userId) {
        List<AdminRequest> requests = adminRequestRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return requests.stream()
                .map(adminRequestMapper::toSummaryDto)
                .toList();
    }

    @Override
    public AdminRequestResponseDto getAdminRequest(Long requestId) {
        AdminRequest adminRequest = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "관리자 요청을 찾을 수 없습니다."));
        
        return adminRequestMapper.toResponseDto(adminRequest);
    }

    @Override
    public long getPendingRequestCountByUserId(Long userId) {
        return adminRequestRepository.countPendingRequestsByUserId(userId);
    }
} 