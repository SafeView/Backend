package com.safeview.domain.user.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 이메일 인증번호 저장소
 *
 * 이메일 인증번호를 메모리에 임시 저장하는 컴포넌트
 * - 인증번호 발송 시 저장
 * - 인증번호 검증 시 조회 및 삭제
 * - 5분 후 자동 만료
 * - 인증 완료된 이메일 관리
 */
@Component
public class EmailVerificationStore {

    private final Map<String, VerificationData> verificationStore = new ConcurrentHashMap<>();
    private final Map<String, Boolean> verifiedEmails = new ConcurrentHashMap<>();

    /**
     * 인증번호 저장
     *
     * @param email 이메일 주소
     * @param code 인증번호
     */
    public void storeVerificationCode(String email, String code) {
        verificationStore.put(email, new VerificationData(code, System.currentTimeMillis()));
    }

    /**
     * 인증번호 검증
     *
     * @param email 이메일 주소
     * @param code 인증번호
     * @return 검증 성공 여부
     */
    public boolean verifyCode(String email, String code) {
        VerificationData data = verificationStore.get(email);
        if (data == null) {
            return false;
        }

        // 5분(300초) 만료 확인
        if (System.currentTimeMillis() - data.getTimestamp() > 300000) {
            verificationStore.remove(email);
            return false;
        }

        // 인증번호 일치 확인
        if (data.getCode().equals(code)) {
            verificationStore.remove(email);
            // 인증 완료 표시
            verifiedEmails.put(email, true);
            return true;
        }

        return false;
    }

    /**
     * 이메일 인증 완료 여부 확인
     *
     * @param email 이메일 주소
     * @return 인증 완료 여부
     */
    public boolean isEmailVerified(String email) {
        return verifiedEmails.getOrDefault(email, false);
    }

    /**
     * 인증번호 데이터 클래스
     */
    private static class VerificationData {
        private final String code;
        private final long timestamp;

        public VerificationData(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }

        public String getCode() {
            return code;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
