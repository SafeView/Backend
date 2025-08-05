package com.safeview.domain.administrator.service;

import com.safeview.domain.administrator.dto.AdminRequestProcessDto;
import com.safeview.domain.administrator.dto.AdminRequestResponseDto;
import com.safeview.domain.administrator.dto.AdminRequestSummaryForAdminDto;
import com.safeview.domain.administrator.entity.AdminRequest;
import com.safeview.domain.administrator.entity.AdminRequestStatus;
import com.safeview.domain.administrator.mapper.AdminRequestMapper;
import com.safeview.domain.administrator.repository.AdminRequestRepository;
import com.safeview.domain.user.entity.Role;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.repository.UserRepository;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminManagementServiceImpl implements AdminManagementService {

    private final AdminRequestRepository adminRequestRepository;
    private final AdminRequestMapper adminRequestMapper;
    private final UserRepository userRepository;

    @Override
    public List<AdminRequestSummaryForAdminDto> getAllRequests() {
        List<AdminRequest> requests = adminRequestRepository.findAllByOrderByCreatedAtDesc();
        return requests.stream()
                .map(adminRequestMapper::toSummaryForAdminDto)
                .toList();
    }

    @Override
    public List<AdminRequestSummaryForAdminDto> getRequestsByStatus(AdminRequestStatus status) {
        List<AdminRequest> requests = adminRequestRepository.findByStatusOrderByCreatedAtDesc(status);
        return requests.stream()
                .map(adminRequestMapper::toSummaryForAdminDto)
                .toList();
    }

    @Override
    @Transactional
    public AdminRequestResponseDto approveRequest(Long requestId, Long adminId, String adminComment) {
        log.info("권한 요청 승인 시작: requestId={}, adminId={}", requestId, adminId);
        
        AdminRequest adminRequest = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "관리자 요청을 찾을 수 없습니다."));

        // 이미 처리된 요청인지 확인
        if (adminRequest.getStatus() != AdminRequestStatus.PENDING) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "이미 처리된 요청입니다.");
        }

        adminRequest.approve(adminComment, adminId);
        
        // 권한 요청 승인 시 사용자 역할 변경
        updateUserRole(adminRequest);

        AdminRequest savedRequest = adminRequestRepository.save(adminRequest);
        log.info("권한 요청 승인 완료: requestId={}, status={}", requestId, savedRequest.getStatus());
        
        return adminRequestMapper.toResponseDto(savedRequest);
    }

    @Override
    @Transactional
    public AdminRequestResponseDto rejectRequest(Long requestId, Long adminId, String adminComment) {
        AdminRequest adminRequest = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "관리자 요청을 찾을 수 없습니다."));

        // 이미 처리된 요청인지 확인
        if (adminRequest.getStatus() != AdminRequestStatus.PENDING) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "이미 처리된 요청입니다.");
        }

        adminRequest.reject(adminComment, adminId);

        AdminRequest savedRequest = adminRequestRepository.save(adminRequest);
        return adminRequestMapper.toResponseDto(savedRequest);
    }

    /**
     * 권한 요청 승인 시 사용자 역할 변경
     */
    private void updateUserRole(AdminRequest adminRequest) {
        User user = userRepository.findById(adminRequest.getUserId())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "사용자를 찾을 수 없습니다."));

        // requestType에 관계없이 USER인 경우 항상 MODERATOR로 변경
        if (user.getRole() == Role.USER) {
            user.updateRole(Role.MODERATOR);
            userRepository.save(user);
        }
    }

    @Override
    public List<AdminRequestSummaryForAdminDto> getPendingRequests() {
        List<AdminRequest> requests = adminRequestRepository.findByStatusOrderByCreatedAtDesc(AdminRequestStatus.PENDING);
        return requests.stream()
                .map(adminRequestMapper::toSummaryForAdminDto)
                .toList();
    }

    @Override
    public AdminRequestResponseDto getRequestDetail(Long requestId) {
        AdminRequest adminRequest = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "관리자 요청을 찾을 수 없습니다."));
        
        return adminRequestMapper.toResponseDto(adminRequest);
    }
} 