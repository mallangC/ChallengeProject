package com.zerobase.challengeproject.member.components.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * JWT 기반의 인가 필터 클래스.
 * HTTP 요청에서 JWT를 검증하고, 유효한 경우 SecurityContext에 인증 정보를 설정.
 * OncePerRequestFilter를 상속하여 요청 당 한 번만 실행.
 */
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * HTTP 요청에서 JWT의 만료 여부를 확인하고, 만약 토큰이 유효하다면 SecurityContext에 인증 정보를 설정.
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 필터 처리 중 발생한 예외
     * @throws IOException      I/O 처리 중 발생한 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null && jwtUtil.isTokenValid(token)) {
            String memberId = jwtUtil.extractMemberId(token);
            UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(memberId);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        if (request.getRequestURI().equals("/api/member/token/refresh")) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("refreshToken")) {
                        token = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    }
                }
            }
            token.substring(7);
            if(jwtUtil.isTokenValid(token)){
                String memberId = jwtUtil.extractMemberId(token);
                UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(memberId);
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰을 추출합니다.
     * 헤더에서 "Authorization" 필드를 확인하고, "Bearer "로 시작하는 경우 토큰을 반환.
     * 그렇지 않으면 `null`을 반환
     *
     * @param request HTTP 요청 객체
     * @return "Bearer "로 시작하는 JWT 토큰 문자열 또는 토큰이 없는 경우 `null`
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
