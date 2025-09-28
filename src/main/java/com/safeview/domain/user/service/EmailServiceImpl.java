package com.safeview.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * 이메일 서비스 구현체
 *
 * 이메일 발송 관련 기능을 담당합니다.
 * - 임시 비밀번호 발송
 * - 이메일 인증번호 발송
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 임시 비밀번호 발송
     *
     * @param email 수신자 이메일
     * @param tempPassword 임시 비밀번호
     */
    @Override
    public void sendTempPassword(String email, String tempPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(email);
            helper.setFrom(fromEmail);
            helper.setSubject("[SafeView] 임시 비밀번호 발송");
            
            String htmlContent = String.format("""
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
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("임시 비밀번호 발송 완료: {}", email);
        } catch (MessagingException e) {
            log.error("임시 비밀번호 발송 실패: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 이메일 인증번호 발송
     *
     * @param email 수신자 이메일
     * @param verificationCode 인증번호
     */
    @Override
    public void sendVerificationCode(String email, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(email);
            helper.setFrom(fromEmail);
            helper.setSubject("[SafeView] 회원가입 인증번호");
            
            String htmlContent = String.format("""
                <div style="font-family: 'Noto Sans', sans-serif; padding: 40px; background-color: #141414; color: #ffffff; border-radius: 10px; max-width: 500px; margin: auto;">
                    <h1 style="color: #e50914; font-size: 24px; margin-bottom: 20px;">SafeView 회원가입 인증</h1>
                    <p style="font-size: 16px; margin-bottom: 10px;">회원가입을 위한 인증번호를 발송해드립니다.</p>
                    <div style="background-color: #333; padding: 20px; font-size: 24px; font-weight: bold; text-align: center; border-radius: 5px; letter-spacing: 2px; margin: 20px 0;">
                        %s
                    </div>
                    <div style="background-color: #2a2a2a; padding: 20px; border-radius: 8px; margin: 20px 0;">
                        <h3 style="color: #ff6b6b; margin-top: 0;">⚠️ 안내사항</h3>
                        <ul style="color: #cccccc; line-height: 1.6;">
                            <li>인증번호는 5분간 유효합니다.</li>
                            <li>인증번호를 정확히 입력해주세요.</li>
                            <li>본인이 요청하지 않은 경우 이 이메일을 무시해주세요.</li>
                        </ul>
                    </div>
                    <p style="font-size: 14px; color: #aaaaaa; margin-top: 30px;">
                        이 메일은 자동 발송되었으며 회신하지 마십시오. 문제가 있는 경우 gl021414@naver.com 으로 문의해주세요.
                    </p>
                    <hr style="border: none; border-top: 1px solid #444; margin: 30px 0;" />
                    <p style="font-size: 13px; color: #888;">© 2025 SafeView. All rights reserved.</p>
                </div>
                """, verificationCode);
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("인증번호 발송 완료: {}", email);
        } catch (MessagingException e) {
            log.error("인증번호 발송 실패: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
}
