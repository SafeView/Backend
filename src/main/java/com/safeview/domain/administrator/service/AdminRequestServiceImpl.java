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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관리자 권한 요청 서비스 구현체
 * 
 * 사용자가 관리자 권한을 요청하고 관리하는 비즈니스 로직을 담당합니다.
 * - 권한 요청 생성
 * - 사용자별 권한 요청 조회
 * - 권한 요청 상세 조회
 * - 대기중인 요청 개수 조회
 * 
 * 보안: 사용자별 요청 접근 권한 검증
 * 감사: 요청 생성 및 조회 이력 관리
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminRequestServiceImpl implements AdminRequestService {

    private final AdminRequestRepository adminRequestRepository;
    private final AdminRequestMapper adminRequestMapper;

    /**
     * 관리자 권한 요청 생성
     * 
     * @param userId 권한을 요청하는 사용자 ID
     * @param createDto 권한 요청 생성 정보 (제목, 설명)
     * @return 생성된 권한 요청 정보
     * 
     * 처리 과정:
     * 1. 입력 값 검증
     * 2. 중복 요청 확인
     * 3. DTO를 엔티티로 변환
     * 4. 데이터베이스에 저장
     * 5. 응답 DTO로 변환하여 반환
     * 
     * 초기 상태: PENDING (대기중)
     * 감사 로그: 요청 생성 이력 기록
     */
    @Override
    @Transactional
    public AdminRequestResponseDto createAdminRequest(Long userId, AdminRequestCreateDto createDto) {
        log.info("관리자 권한 요청 생성 시작: userId={}", userId);
        
        try {
            // 입력 값 검증
            if (createDto == null) {
                throw new ApiException(ErrorCode.BAD_REQUEST, "요청 정보가 필요합니다.");
            }
            if (createDto.getTitle() == null || createDto.getTitle().trim().isEmpty()) {
                throw new ApiException(ErrorCode.BAD_REQUEST, "제목을 입력해주세요.");
            }
            if (createDto.getDescription() == null || createDto.getDescription().trim().isEmpty()) {
                throw new ApiException(ErrorCode.BAD_REQUEST, "설명을 입력해주세요.");
            }
            
            // 중복 요청 확인 (대기중인 요청이 있는지 확인)
            long pendingCount = adminRequestRepository.countPendingRequestsByUserId(userId);
            if (pendingCount > 0) {
                throw new ApiException(ErrorCode.BAD_REQUEST, "이미 대기중인 권한 요청이 있습니다.");
            }
            
            AdminRequest adminRequest = adminRequestMapper.toEntity(userId, createDto);
            AdminRequest savedRequest = adminRequestRepository.save(adminRequest);
            
            log.info("관리자 권한 요청 생성 완료: requestId={}, status={}", savedRequest.getId(), savedRequest.getStatus());
            return adminRequestMapper.toResponseDto(savedRequest);
        } catch (ApiException e) {
            log.error("관리자 권한 요청 생성 실패: userId={}, error={}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("관리자 권한 요청 생성 중 예상치 못한 오류: userId={}", userId, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "권한 요청 생성 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자별 권한 요청 목록 조회
     * 
     * @param userId 조회할 사용자 ID
     * @return 해당 사용자의 모든 권한 요청 목록 (최신순 정렬)
     * 
     * 기능: 특정 사용자가 제출한 모든 권한 요청 조회
     * 정렬: 생성일시 기준 내림차순 (최신 요청이 먼저)
     * 용도: 사용자가 자신의 요청 이력 확인
     * 
     * 보안: 사용자별 접근 권한 검증 필요
     */
    @Override
    public List<AdminRequestSummaryDto> getUserRequests(Long userId) {
        log.info("사용자 권한 요청 목록 조회: userId={}", userId);
        
        try {
            List<AdminRequest> requests = adminRequestRepository.findByUserIdOrderByCreatedAtDesc(userId);
            return requests.stream()
                    .map(adminRequestMapper::toSummaryDto)
                    .toList();
        } catch (ApiException e) {
            log.error("사용자 권한 요청 목록 조회 실패: userId={}, error={}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 권한 요청 목록 조회 중 예상치 못한 오류: userId={}", userId, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "요청 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 권한 요청 상세 조회
     * 
     * @param requestId 조회할 권한 요청 ID
     * @return 특정 권한 요청의 상세 정보
     * 
     * 기능: 요청의 모든 상세 정보 조회
     * 포함: 요청 내용, 상태, 관리자 코멘트, 처리 시간 등
     * 
     * 예외: 존재하지 않는 요청
     * 보안: 요청 접근 권한 검증 필요
     */
    @Override
    public AdminRequestResponseDto getAdminRequest(Long requestId) {
        log.info("권한 요청 상세 조회: requestId={}", requestId);
        
        try {
            // 입력 값 검증
            if (requestId == null || requestId <= 0) {
                throw new ApiException(ErrorCode.BAD_REQUEST, "유효하지 않은 요청 ID입니다.");
            }
            
            AdminRequest adminRequest = adminRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "관리자 요청을 찾을 수 없습니다."));
            
            return adminRequestMapper.toResponseDto(adminRequest);
        } catch (ApiException e) {
            log.error("권한 요청 상세 조회 실패: requestId={}, error={}", requestId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("권한 요청 상세 조회 중 예상치 못한 오류: requestId={}", requestId, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "요청 상세 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자별 대기중인 권한 요청 개수 조회
     * 
     * @param userId 조회할 사용자 ID
     * @return 해당 사용자의 PENDING 상태 요청 개수
     * 
     * 기능: 특정 사용자가 제출한 대기중인 요청의 개수 조회
     * 용도: 중복 요청 방지, 요청 상태 확인
     * 
     * 보안: 사용자별 접근 권한 검증 필요
     */
    @Override
    public long getPendingRequestCountByUserId(Long userId) {
        log.info("사용자 대기중인 요청 개수 조회: userId={}", userId);
        
        try {
            return adminRequestRepository.countPendingRequestsByUserId(userId);
        } catch (ApiException e) {
            log.error("사용자 대기중인 요청 개수 조회 실패: userId={}, error={}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 대기중인 요청 개수 조회 중 예상치 못한 오류: userId={}", userId, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "대기중인 요청 개수 조회 중 오류가 발생했습니다.");
        }
    }
} 