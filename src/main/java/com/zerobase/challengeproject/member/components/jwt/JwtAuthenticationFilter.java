package com.zerobase.challengeproject.member.components.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.challengeproject.exception.BlacklistedMemberException;
import com.zerobase.challengeproject.member.domain.form.MemberLoginForm;
import com.zerobase.challengeproject.type.MemberType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 기반의 인증 필터 클래스.
 * 사용자의 로그인 요청을 처리하고, 인증 성공 시 JWT를 생성하여 반환.
 * UsernamePasswordAuthenticationFilter를 확장하여 사용자 인증을 담당.
 */
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        this.jwtUtil = jwtUtil;
    }
    /**
     * 로그인 시도 처리 메서드.
     * HTTP 요청의 입력 스트림에서 JSON 데이터를 읽어 로그인 정보를 파싱한 후, 인증을 시도.
     *
     * @param request  클라이언트의 HTTP 요청.
     * @param response 서버의 HTTP 응답.
     * @return Authentication 객체 (인증 성공 시 SecurityContext에 저장됨)
     * @throws AuthenticationException 인증 실패 시 예외 발생.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            /**
             * JSON 요청 데이터를 파싱하여 로그인 정보 추출
              */
            MemberLoginForm form = new ObjectMapper().readValue(request.getInputStream(), MemberLoginForm.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(form.getLoginId(),
                            form.getPassword())
            );
        } catch (IOException e) {
            throw new AuthenticationServiceException("로그인 시도 중 오류가 발생했습니다.", e);
        }
    }
    /**
     * 인증 성공 시 호출되는 메서드.
     * 사용자 정보를 바탕으로 JWT를 생성하고 응답 헤더 및 본문에 추가
     *
     * @param request      클라이언트의 HTTP 요청.
     * @param response     서버의 HTTP 응답.
     * @param chain        필터 체인.
     * @param authResult   인증 결과 (UserDetailsImpl 객체 포함)
     * @throws IOException 입출력 예외 발생 시.
     * @throws ServletException 서블릿 관련 예외 발생 시.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        MemberType role = userDetails.getMember().getMemberType();
        String loginId = userDetails.getMember().getLoginId();
        String accessToken = jwtUtil.generateAccessToken(loginId, role);
        String refreshToken = jwtUtil.generateRefreshToken(loginId, role);
        ResponseCookie refreshTokenCookie = jwtUtil.createRefreshTokenCookie(refreshToken, 7);

        /**
         * JWT를 응답 헤더에 추가
          */
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        writeJsonResponse(response, HttpStatus.OK, "로그인에 성공했습니다", userDetails.getMember().getLoginId(),accessToken);
    }
    /**
     * 인증 실패 시 호출되는 메서드.
     * HTTP 상태 코드를 401 (Unauthorized)로 설정하고, JSON 형태의 에러 메시지를 응답.
     *
     * @param request  클라이언트의 HTTP 요청.
     * @param response 서버의 HTTP 응답.
     * @param failed   인증 실패 예외 객체.
     * @throws IOException 입출력 예외 발생 시.
     * @throws ServletException 서블릿 관련 예외 발생 시.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        String message = failed.getMessage();

        if(failed instanceof BadCredentialsException){
            message = "아이디 또는 비밀번호가 잘못되었습니다.";
        } else if (failed instanceof BlacklistedMemberException) {
            BlacklistedMemberException ex = (BlacklistedMemberException) failed;
            message = ex.getMessage();
        }
        log.warn(message);
        writeJsonResponse(response, HttpStatus.UNAUTHORIZED, message, null, null);
    }

    private void writeJsonResponse(HttpServletResponse response, HttpStatus status, String message, String loginId, String token ) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> data = new HashMap<>();
        if (loginId != null) data.put("loginId", loginId);
        if (token != null) data.put("token", token);

        Map<String, Object> body = Map.of(
                "status", status,
                "message", message,
                "data", data
        );

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }
}
