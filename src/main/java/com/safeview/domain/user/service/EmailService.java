package com.safeview.domain.user.service;

/**
 * 이메일 서비스 인터페이스
 *
 * 이메일 발송 관련 기능을 담당합니다.
 * - 임시 비밀번호 발송
 * - 이메일 인증번호 발송
 */
public interface EmailService {

    /**
     * 임시 비밀번호 발송
     *
     * @param email 수신자 이메일
     * @param tempPassword 임시 비밀번호
     */
    void sendTempPassword(String email, String tempPassword);

    /**
     * 이메일 인증번호 발송
     *
     * @param email 수신자 이메일
     * @param verificationCode 인증번호
     */
    void sendVerificationCode(String email, String verificationCode);
}
