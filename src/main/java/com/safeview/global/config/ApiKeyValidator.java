package com.safeview.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

/*
 * API 키 검증 클래스
 * 
 * AI 서버와의 통신 시 사용되는 API 키를 검증하는 클래스
 * 설정 파일에서 관리되는 유효한 API 키와 비교하여 검증
 */
@Component
public class ApiKeyValidator {
    @Value("${api.internal.ai-server-key}")
    private String validApiKey;

    /*
     * API 키 유효성 검증
     * 
     * @param providedApiKey 검증할 API 키
     * @return 유효한 API 키인지 여부
     * 
     * 보안: MessageDigest.isEqual을 사용하여 타이밍 공격 방지
     */
    public boolean isValidApiKey(String providedApiKey){
        if (providedApiKey == null || providedApiKey.trim().isEmpty()) return false;

        return MessageDigest.isEqual(
                validApiKey.getBytes(),
                providedApiKey.getBytes()
        );
    }
}
