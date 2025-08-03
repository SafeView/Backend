package com.safeview.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Component
public class ApiKeyValidator {
    @Value("${api.internal.ai-server-key}")
    private String validApiKey;

    public boolean isValidApiKey(String providedApiKey){
        if (providedApiKey == null || providedApiKey.trim().isEmpty()) return false;

        return MessageDigest.isEqual(
                validApiKey.getBytes(),
                providedApiKey.getBytes()
        );
    }
}
