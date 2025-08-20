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
     * 1. DTO를 엔티티로 변환
     * 2. 데이터베이스에 저장
     * 3. 응답 DTO로 변환하여 반환
     * 
     * 초기 상태: PENDING (대기중)
     * 감사 로그: 요청 생성 이력 기록
     */
    @Override
    @Transactional
    public AdminRequestResponseDto createAdminRequest(Long userId, AdminRequestCreateDto createDto) {
        log.info("관리자 권한 요청 생성 시작: userId={}", userId);
        
        AdminRequest adminRequest = adminRequestMapper.toEntity(userId, createDto);
        AdminRequest savedRequest = adminRequestRepository.save(adminRequest);
        
        log.info("관리자 권한 요청 생성 완료: requestId={}, status={}", savedRequest.getId(), savedRequest.getStatus());
        return adminRequestMapper.toResponseDto(savedRequest);
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
        
        List<AdminRequest> requests = adminRequestRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return requests.stream()
                .map(adminRequestMapper::toSummaryDto)
                .toList();
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
        
        AdminRequest adminRequest = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "관리자 요청을 찾을 수 없습니다."));
        
        return adminRequestMapper.toResponseDto(adminRequest);
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
        
        return adminRequestRepository.countPendingRequestsByUserId(userId);
    }
} 