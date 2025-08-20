package com.safeview.global.security;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


/*
 * JWT 인증 진입점 클래스
 * 
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출되는 클래스
 * 401 Unauthorized 응답을 반환하여 인증 실패를 처리
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /*
     * 인증 실패 처리
     * 
     * 인증되지 않은 사용자의 접근 시 401 Unauthorized 응답 반환
     * JSON 형태의 에러 메시지를 포함하여 클라이언트에게 인증 필요성 알림
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        // 🔐 인증 실패 → 401 상태 코드 반환
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // 메시지를 JSON 형태로 반환
        response.getWriter().write("{\"error\": \"Unauthorized access. Please log in.\"}");
    }
}
