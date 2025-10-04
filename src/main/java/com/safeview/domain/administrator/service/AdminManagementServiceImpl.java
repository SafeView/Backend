package com.safeview.domain.administrator.service;

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

/**
 * 관리자 권한 요청 관리 서비스 구현체
 * 
 * ADMIN 권한을 가진 관리자가 사용자의 권한 요청을 처리하는 비즈니스 로직을 담당합니다.
 * - 권한 요청 조회 (전체, 상태별, 대기중)
 * - 권한 요청 승인/거절 처리
 * - 사용자 역할 변경 (USER → MODERATOR)
 * 
 * 보안: 권한 검증 및 역할 변경 로직
 * 감사: 처리 이력 및 관리자 코멘트 기록
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminManagementServiceImpl implements AdminManagementService {

    private final AdminRequestRepository adminRequestRepository;
    private final AdminRequestMapper adminRequestMapper;
    private final UserRepository userRepository;


    /**
     * 모든 권한 요청 목록 조회 (관리자용)
     * 
     * @return 모든 권한 요청 목록 (최신순 정렬)
     * 
     * 기능: 관리자가 모든 권한 요청을 조회
     * 정렬: 생성일시 기준 내림차순 (최신 요청이 먼저)
     * 용도: 전체 요청 현황 파악
     */
    @Override
    public List<AdminRequestSummaryForAdminDto> getAllRequests() {
        try {
            List<AdminRequest> requests = adminRequestRepository.findAllByOrderByCreatedAtDesc();
            return requests.stream()
                    .map(adminRequestMapper::toSummaryForAdminDto)
                    .toList();
        } catch (Exception e) {
            log.error("전체 권한 요청 목록 조회 중 오류 발생", e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "전체 요청 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 상태별 권한 요청 목록 조회
     * 
     * @param status 조회할 요청 상태 (PENDING, APPROVED, REJECTED)
     * @return 특정 상태의 권한 요청 목록 (최신순 정렬)
     * 
     * 기능: 상태별로 요청을 필터링하여 조회
     * 용도: 승인/거절 통계, 처리 이력 확인
     */
    @Override
    public List<AdminRequestSummaryForAdminDto> getRequestsByStatus(AdminRequestStatus status) {
        try {
            List<AdminRequest> requests = adminRequestRepository.findByStatusOrderByCreatedAtDesc(status);
            return requests.stream()
                    .map(adminRequestMapper::toSummaryForAdminDto)
                    .toList();
        } catch (ApiException e) {
            log.error("상태별 권한 요청 목록 조회 실패: status={}, error={}", status, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("상태별 권한 요청 목록 조회 중 예상치 못한 오류: status={}", status, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "상태별 요청 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 권한 요청 승인 처리
     * 
     * @param requestId 승인할 권한 요청 ID
     * @param adminId 승인하는 관리자 ID
     * @param adminComment 관리자 코멘트
     * @return 승인 처리된 권한 요청 정보
     * 
     * 처리 과정:
     * 1. 입력 값 검증
     * 2. 요청 존재 여부 확인
     * 3. 요청 상태 검증 (PENDING인지 확인)
     * 4. 요청 상태를 APPROVED로 변경
     * 5. 사용자 역할을 USER → MODERATOR로 업그레이드
     * 6. 처리 이력 저장 (관리자 ID, 코멘트, 처리 시간)
     * 
     * 권한 변경: 사용자 역할 업그레이드
     * 감사 로그: 승인 처리 이력 기록
     * 예외: 이미 처리된 요청, 존재하지 않는 요청
     */
    @Override
    @Transactional
    public AdminRequestResponseDto approveRequest(Long requestId, Long adminId, String adminComment) {
        log.info("권한 요청 승인 시작: requestId={}, adminId={}", requestId, adminId);

        try {
            // 입력 값 검증
            AdminRequest adminRequest = adminRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "유효하지 않은 요청입니다."));

            // 요청 상태 검증
            if (adminRequest.getStatus() != AdminRequestStatus.PENDING) {
                throw new ApiException(ErrorCode.BAD_REQUEST, "이미 처리된 요청입니다.");
            }
            
            // 승인 처리
            adminRequest.approve(adminComment, adminId);
            updateUserRole(adminRequest);

            AdminRequest savedRequest = adminRequestRepository.save(adminRequest);
            log.info("권한 요청 승인 완료: requestId={}, status={}", requestId, savedRequest.getStatus());
            
            return adminRequestMapper.toResponseDto(savedRequest);
        } catch (ApiException e) {
            log.error("권한 요청 승인 실패: requestId={}, adminId={}, error={}", requestId, adminId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("권한 요청 승인 중 예상치 못한 오류: requestId={}, adminId={}", requestId, adminId, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "권한 요청 승인 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 권한 요청 거절 처리
     * 
     * @param requestId 거절할 권한 요청 ID
     * @param adminId 거절하는 관리자 ID
     * @param adminComment 관리자 코멘트
     * @return 거절 처리된 권한 요청 정보
     * 
     * 처리 과정:
     * 1. 입력 값 검증
     * 2. 요청 존재 여부 확인
     * 3. 요청 상태 검증 (PENDING인지 확인)
     * 4. 요청 상태를 REJECTED로 변경
     * 5. 처리 이력 저장 (관리자 ID, 코멘트, 처리 시간)
     * 
     * 권한 유지: 사용자 역할 변경 없음 (USER 유지)
     * 감사 로그: 거절 처리 이력 기록
     * 예외: 이미 처리된 요청, 존재하지 않는 요청
     */
    @Override
    @Transactional
    public AdminRequestResponseDto rejectRequest(Long requestId, Long adminId, String adminComment) {
        log.info("권한 요청 거절 시작: requestId={}, adminId={}", requestId, adminId);
        
        try {
            // 입력 값 검증
            if (requestId == null || requestId <= 0) {
                throw new ApiException(ErrorCode.BAD_REQUEST, "유효하지 않은 요청 ID입니다.");
            }

            AdminRequest adminRequest = adminRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "유효하지 않은 요청입니다."));

            // 이미 처리된 요청인지 확인
            if (adminRequest.getStatus() != AdminRequestStatus.PENDING) {
                throw new ApiException(ErrorCode.BAD_REQUEST, "이미 처리된 요청입니다.");
            }

            // 요청 거절 처리 (상태 변경, 코멘트 저장, 처리 시간 기록)
            adminRequest.reject(adminComment, adminId);

            AdminRequest savedRequest = adminRequestRepository.save(adminRequest);
            log.info("권한 요청 거절 완료: requestId={}, status={}", requestId, savedRequest.getStatus());
            
            return adminRequestMapper.toResponseDto(savedRequest);
        } catch (ApiException e) {
            log.error("권한 요청 거절 실패: requestId={}, adminId={}, error={}", requestId, adminId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("권한 요청 거절 중 예상치 못한 오류: requestId={}, adminId={}", requestId, adminId, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "권한 요청 거절 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 권한 요청 승인 시 사용자 역할 변경
     * 
     * @param adminRequest 승인된 권한 요청
     * 
     * 역할 변경 로직:
     * - USER → MODERATOR로 변경
     * - 다른 역할은 변경하지 않음
     * 
     * 예외: 사용자가 존재하지 않는 경우
     */
    private void updateUserRole(AdminRequest adminRequest) {
        try {
            User user = userRepository.findById(adminRequest.getUserId())
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

            // 역할 변경 검증 및 실행
            if (user.getRole() == Role.USER) {
                user.updateRole(Role.MODERATOR);
                userRepository.save(user);
                log.info("사용자 역할 변경 완료: userId={}, oldRole={}, newRole={}", 
                        user.getId(), Role.USER, Role.MODERATOR);
            } else {
                log.warn("사용자 역할 변경 불필요: userId={}, currentRole={}", user.getId(), user.getRole());
            }
        } catch (ApiException e) {
            log.error("사용자 역할 변경 실패: userId={}, error={}", adminRequest.getUserId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 역할 변경 중 예상치 못한 오류: userId={}", adminRequest.getUserId(), e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 역할 변경 중 오류가 발생했습니다.");
        }
    }

    /**
     * 대기중인 권한 요청 목록 조회
     * 
     * @return PENDING 상태의 권한 요청 목록 (최신순 정렬)
     * 
     * 기능: 처리 대기중인 요청만 필터링하여 조회
     * 용도: 관리자가 우선적으로 처리할 요청 확인
     */
    @Override
    public List<AdminRequestSummaryForAdminDto> getPendingRequests() {
        try {
            List<AdminRequest> requests = adminRequestRepository.findByStatusOrderByCreatedAtDesc(AdminRequestStatus.PENDING);
            return requests.stream()
                    .map(adminRequestMapper::toSummaryForAdminDto)
                    .toList();
        } catch (Exception e) {
            log.error("대기중인 권한 요청 목록 조회 중 오류 발생", e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "대기중인 요청 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 권한 요청 상세 조회
     * 
     * @param requestId 조회할 권한 요청 ID
     * @return 특정 권한 요청의 상세 정보
     * 
     * 기능: 요청의 모든 상세 정보 조회
     * 포함: 사용자 정보, 요청 내용, 처리 상태, 관리자 코멘트 등
     * 예외: 존재하지 않는 요청
     */
    @Override
    public AdminRequestResponseDto getRequestDetail(Long requestId) {
        try {
            AdminRequest adminRequest = adminRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "유효하지 않은 요청입니다."));
            
            return adminRequestMapper.toResponseDto(adminRequest);
        } catch (ApiException e) {
            log.error("권한 요청 상세 조회 실패: requestId={}, error={}", requestId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("권한 요청 상세 조회 중 예상치 못한 오류: requestId={}", requestId, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "요청 상세 조회 중 오류가 발생했습니다.");
        }
    }
} 