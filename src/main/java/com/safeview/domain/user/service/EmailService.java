package com.safeview.domain.user.service;

/*
 * 이메일 발송 서비스 인터페이스
 * 
 * 인증번호 이메일 발송을 담당하는 서비스
 */
public interface EmailService {
    
    /**
     * 인증번호 이메일 발송
     * 
     * @param email 수신자 이메일
     * @param verificationCode 인증번호
     * @return 발송 성공 여부
     */
    boolean sendVerificationCode(String email, String verificationCode);
    
    /**
     * 인증번호 생성
     * 
     * @return 6자리 랜덤 인증번호
     */
    String generateVerificationCode();
    
    /**
     * 임시 비밀번호 이메일 발송
     * 
     * @param email 수신자 이메일
     * @param tempPassword 임시 비밀번호
     * @return 발송 성공 여부
     */
    boolean sendTempPassword(String email, String tempPassword);
}
