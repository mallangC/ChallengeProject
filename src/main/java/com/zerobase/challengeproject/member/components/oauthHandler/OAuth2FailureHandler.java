package com.zerobase.challengeproject.member.components.oauthHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {
    /***
     * OAuth 로그인 실패 시 호출 되는 메서드입니다.
     * @param request 클라이언트의 HTTP 요청
     * @param response 서버의 HTTP 응답
     * @param exception 인증 실패 원인을 담고 있는 예외
     * @throws IOException 입출력 예외가 발생한 경우
     * @throws ServletException 서블릿 관련 예외가 발생할 경우
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String message = "OAuth2 로그인 실패: " + exception.getMessage();

        new ObjectMapper().writeValue(response.getWriter(), Map.of(
                "error", message,
                "exception", exception
        ));
    }
}
