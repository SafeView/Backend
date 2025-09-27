package com.safeview.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Random;

/*
 * 이메일 발송 서비스 구현체
 * 
 * 실제 이메일 발송을 담당하는 서비스
 * 개발 환경에서는 콘솔에 로그로 출력, 운영 환경에서는 실제 이메일 발송
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    
    private final Random random = new Random();
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    @Value("${spring.mail.username:}")
    private String emailUsername;
    
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    /**
     * 인증번호 이메일 발송
     * 
     * @param email 수신자 이메일
     * @param verificationCode 인증번호
     * @return 발송 성공 여부
     */
    @Override
    public boolean sendVerificationCode(String email, String verificationCode) {
        try {
            if (emailUsername.isEmpty()) {
                // 개발 환경에서는 콘솔에 로그 출력
                log.info("=".repeat(50));
                log.info("이메일 발송");
                log.info("수신자: {}", email);
                log.info("인증번호: {}", verificationCode);
                log.info("유효시간: 5분");
                log.info("=".repeat(50));
                log.info("실제 이메일 발송을 위해서는 이메일 설정이 필요합니다.");
                return true;
            }
            
            // 실제 이메일 발송
            return sendEmail(email, verificationCode);
            
        } catch (Exception e) {
            log.error("이메일 발송 실패: email={}, error={}", email, e.getMessage());
            return false;
        }
    }
    
    /**
     * 실제 이메일 발송 (HTML 형식)
     */
    private boolean sendEmail(String email, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("[SafeView] 비밀번호 재설정 인증번호");
            helper.setText(createEmailContent(verificationCode), true); // true = HTML 형식
            
            mailSender.send(message);
            
            log.info("이메일 발송 성공: email={}, code={}", email, verificationCode);
            return true;
            
        } catch (MessagingException e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 이메일 내용 생성 
     */
    private String createEmailContent(String verificationCode) {
        return String.format("""
            <div style="font-family: 'Noto Sans', sans-serif; padding: 40px; background-color: #141414; color: #ffffff; border-radius: 10px; max-width: 500px; margin: auto;">
                <h1 style="color: #e50914; font-size: 24px; margin-bottom: 20px;">SafeView 인증 코드</h1>
                <p style="font-size: 16px; margin-bottom: 10px;">아래 코드를 5분 이내에 입력해 주세요.</p>
                <div style="background-color: #333; padding: 20px; font-size: 28px; font-weight: bold; text-align: center; border-radius: 5px; letter-spacing: 2px;">
                    %s
                </div>
                <p style="font-size: 14px; color: #aaaaaa; margin-top: 30px;">
                    이 메일은 자동 발송되었으며 회신하지 마십시오. 문제가 있는 경우 gl021414@naver.com 으로 문의해주세요.
                </p>
                <hr style="border: none; border-top: 1px solid #444; margin: 30px 0;" />
                <p style="font-size: 13px; color: #888;">© 2025 SafeView. All rights reserved.</p>
            </div>
            """, verificationCode);
    }
    
    /**
     * 인증번호 생성
     * 
     * @return 6자리 랜덤 인증번호
     */
    @Override
    public String generateVerificationCode() {
        return String.format("%06d", random.nextInt(1000000));
    }
    
    /**
     * 임시 비밀번호 이메일 발송
     * 
     * @param email 수신자 이메일 주소
     * @param tempPassword 임시 비밀번호
     * @return 발송 성공 여부
     */
    @Override
    public boolean sendTempPassword(String email, String tempPassword) {
        try {
            if (emailUsername.isEmpty()) {
                // 개발 환경에서는 콘솔에 로그 출력
                log.info("=".repeat(50));
                log.info("[개발모드] 임시 비밀번호 이메일 발송");
                log.info("수신자: {}", email);
                log.info("=".repeat(50));
                log.info("실제 이메일 발송을 위해서는 이메일 설정이 필요합니다.");
                return true;
            }
            
            return sendTempPasswordEmail(email, tempPassword);
        } catch (Exception e) {
            log.error("임시 비밀번호 이메일 발송 실패: email={}, error={}", email, e.getMessage());
            return false;
        }
    }

    /**
     * 실제 임시 비밀번호 이메일 발송 (HTML 형식)
     */
    private boolean sendTempPasswordEmail(String email, String tempPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("[SafeView] 임시 비밀번호 발송");
            helper.setText(createTempPasswordEmailContent(tempPassword), true); // true = HTML 형식
            
            mailSender.send(message);
            
            log.info("임시 비밀번호 이메일 발송 성공: email={}", email);
            return true;
            
        } catch (MessagingException e) {
            log.error("임시 비밀번호 이메일 발송 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 임시 비밀번호 이메일 내용 생성
     */
    private String createTempPasswordEmailContent(String tempPassword) {
        return String.format("""
            <div style="font-family: 'Noto Sans', sans-serif; padding: 40px; background-color: #141414; color: #ffffff; border-radius: 10px; max-width: 500px; margin: auto;">
                <h1 style="color: #e50914; font-size: 24px; margin-bottom: 20px;">SafeView 임시 비밀번호</h1>
                <p style="font-size: 16px; margin-bottom: 10px;">비밀번호 찾기 요청에 따라 임시 비밀번호를 발송해드립니다.</p>
                <div style="background-color: #333; padding: 20px; font-size: 24px; font-weight: bold; text-align: center; border-radius: 5px; letter-spacing: 2px; margin: 20px 0;">
                    %s
                </div>
                <div style="background-color: #2a2a2a; padding: 20px; border-radius: 8px; margin: 20px 0;">
                    <h3 style="color: #ff6b6b; margin-top: 0;">⚠️ 보안 안내</h3>
                    <ul style="color: #cccccc; line-height: 1.6;">
                        <li>로그인 후 반드시 비밀번호를 변경해주세요.</li>
                        <li>임시 비밀번호는 타인에게 알려주지 마세요.</li>
                        <li>본인이 요청하지 않은 경우 이 이메일을 무시해주세요.</li>
                    </ul>
                </div>
                <p style="font-size: 14px; color: #aaaaaa; margin-top: 30px;">
                    이 메일은 자동 발송되었으며 회신하지 마십시오. 문제가 있는 경우 gl021414@naver.com 으로 문의해주세요.
                </p>
                <hr style="border: none; border-top: 1px solid #444; margin: 30px 0;" />
                <p style="font-size: 13px; color: #888;">© 2025 SafeView. All rights reserved.</p>
            </div>
            """, tempPassword);
    }
}
