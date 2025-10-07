package com.safeview.domain.dashboard.service;

import com.safeview.domain.dashboard.dto.KeyStatsDto;
import com.safeview.domain.dashboard.dto.UserStatsDto;
import com.safeview.domain.dashboard.dto.YearlyKeyIssuanceDto;
import com.safeview.domain.dashboard.dto.YearlyNewUsersDto;
import com.safeview.domain.dashboard.mapper.DashboardMapper;
import com.safeview.domain.decryption.repository.DecryptionKeyRepository;
import com.safeview.domain.user.entity.Role;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.repository.UserRepository;
import com.safeview.global.exception.ApiException;
import com.safeview.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 대시보드 서비스 구현체
 * 
 * 관리자 대시보드에서 필요한 통계 및 분석 데이터를 제공하는 서비스 구현
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    
    private final UserRepository userRepository;
    private final DecryptionKeyRepository decryptionKeyRepository;
    private final DashboardMapper dashboardMapper;
    
    /**
     * 사용자 통계 조회
     * 
     * @param adminUserId 관리자 사용자 ID
     * @return 사용자 통계 정보
     */
    @Override
    public UserStatsDto getUserStats(Long adminUserId) {
        // 관리자 권한 검증
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "관리자를 찾을 수 없습니다."));
        
        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(ErrorCode.FORBIDDEN, "ADMIN 권한이 없습니다.");
        }
        // 총 사용자 수 조회
        long totalUsers = userRepository.count();
        
        // 역할별 사용자 수 조회
        long userCount = userRepository.countByRole(Role.USER);
        long moderatorCount = userRepository.countByRole(Role.MODERATOR);
        long adminCount = userRepository.countByRole(Role.ADMIN);
        
        // 매퍼를 통해 DTO 변환
        return dashboardMapper.toUserStatsDto(totalUsers, userCount, moderatorCount, adminCount);
    }
    
    /**
     * 1년간 월별 신규 가입자 수 조회
     * 
     * @param adminUserId 관리자 사용자 ID
     * @return 1년간 월별 신규 가입자 수 정보
     */
    @Override
    public YearlyNewUsersDto getYearlyNewUsers(Long adminUserId) {
        // 관리자 권한 검증
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "관리자를 찾을 수 없습니다."));
        
        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(ErrorCode.FORBIDDEN, "ADMIN 권한이 없습니다.");
        }
        int currentYear = LocalDateTime.now().getYear();
        List<YearlyNewUsersDto.MonthlyData> monthlyDataList = new ArrayList<>();
        long totalNewUsers = 0;
        
        // 1월부터 12월까지 각 월의 신규 가입자 수 조회
        for (int month = 1; month <= 12; month++) {
            long newUsersCount = userRepository.countByCreatedAtYearAndMonth(currentYear, month);
            totalNewUsers += newUsersCount;
            
            monthlyDataList.add(YearlyNewUsersDto.MonthlyData.builder()
                    .month(month)
                    .newUsersCount(newUsersCount)
                    .build());
        }
        
        // 매퍼를 통해 DTO 변환
        return dashboardMapper.toYearlyNewUsersDto(currentYear, monthlyDataList, totalNewUsers);
    }
    
    /**
     * 복호화 키 통계 조회
     * 
     * @param adminUserId 관리자 사용자 ID
     * @return 복호화 키 통계 정보
     */
    @Override
    public KeyStatsDto getKeyStats(Long adminUserId) {
        // 관리자 권한 검증
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "관리자를 찾을 수 없습니다."));
        
        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(ErrorCode.FORBIDDEN, "ADMIN 권한이 없습니다.");
        }
        
        // 총 발급된 키 수
        long totalKeys = decryptionKeyRepository.count();
        
        // 상태별 키 수 조회
        long activeKeys = decryptionKeyRepository.countByStatus("ACTIVE");
        long expiredKeys = decryptionKeyRepository.countByStatus("EXPIRED");
        long revokedKeys = decryptionKeyRepository.countByStatus("REVOKED");
        
        // 키 사용률 계산
        Long totalIssuedUses = decryptionKeyRepository.getTotalIssuedUses();
        Long totalUsedUses = decryptionKeyRepository.getTotalUsedUses();
        Double usageRate = 0.0;
        if (totalIssuedUses != null && totalIssuedUses > 0) {
            usageRate = (double) (totalUsedUses != null ? totalUsedUses : 0) / totalIssuedUses;
            // 소수점 둘째 자리까지만 반올림
            usageRate = Math.round(usageRate * 100.0) / 100.0;
        }
        
        // 매퍼를 통해 DTO 변환
        return dashboardMapper.toKeyStatsDto(totalKeys, activeKeys, expiredKeys, revokedKeys, usageRate);
    }
    
    /**
     * 1년간 월별 복호화 키 발급 추이 조회
     * 
     * @param adminUserId 관리자 사용자 ID
     * @return 1년간 월별 복호화 키 발급 추이 정보
     */
    @Override
    public YearlyKeyIssuanceDto getYearlyKeyIssuance(Long adminUserId) {
        // 관리자 권한 검증
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "관리자를 찾을 수 없습니다."));
        
        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(ErrorCode.FORBIDDEN, "ADMIN 권한이 없습니다.");
        }
        
        int currentYear = LocalDateTime.now().getYear();
        List<YearlyKeyIssuanceDto.MonthlyData> monthlyDataList = new ArrayList<>();
        long totalIssuedKeys = 0;
        
        // 1월부터 12월까지 각 월의 키 발급 수 조회
        for (int month = 1; month <= 12; month++) {
            long issuedKeysCount = decryptionKeyRepository.countByIssuedAtYearAndMonth(currentYear, month);
            totalIssuedKeys += issuedKeysCount;
            
            monthlyDataList.add(YearlyKeyIssuanceDto.MonthlyData.builder()
                    .month(month)
                    .issuedKeysCount(issuedKeysCount)
                    .build());
        }
        
        // 매퍼를 통해 DTO 변환
        return dashboardMapper.toYearlyKeyIssuanceDto(currentYear, monthlyDataList, totalIssuedKeys);
    }
}
