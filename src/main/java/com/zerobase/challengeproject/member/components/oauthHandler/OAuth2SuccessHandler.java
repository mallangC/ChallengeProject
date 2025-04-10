package com.zerobase.challengeproject.member.components.oauthHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.challengeproject.member.components.jwt.JwtUtil;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.entity.RefreshToken;
import com.zerobase.challengeproject.member.repository.RefreshTokenRepository;
import com.zerobase.challengeproject.member.service.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * OAuth2 인증이 성공했을 때 호출되는 메서드입니다.
     * 인증된 사용자 정보를 바탕으로 액세스 토큰과 리프레시 토큰을 생성하고,
     * 리프레시 토큰을 DB에 저장하거나 갱신한 후, 해당 토큰 정보를
     * 응답 헤더와 바디에 포함하여 클라이언트에게 전달합니다.
     *
     * @param request        클라이언트의 HTTP 요청
     * @param response       서버의 HTTP 응답
     * @param authentication 인증된 사용자 정보를 포함하는 객체
     * @throws ServletException 서블릿 관련 예외가 발생할 경우
     * @throws IOException      입출력 예외가 발생할 경우
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        Member member = user.getMember();

        String accessToken = jwtUtil.generateAccessToken(member.getLoginId(), member.getMemberType());
        String refreshToken = jwtUtil.generateRefreshToken(member.getLoginId(), member.getMemberType());

        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByLoginId(member.getLoginId());
        // 리프레시 토큰을 회원이 가지고 있는 지 확인 후 토큰 저장
        if(refreshTokenOptional.isPresent()) {
            RefreshToken token = refreshTokenOptional.get();
            token.tokenRenewal(refreshToken, Instant.now().plus(Duration.ofDays(7)));
        }else {
            refreshTokenRepository.save(
                    RefreshToken.builder()
                            .token(refreshToken)
                            .loginId(member.getLoginId())
                            .expireDate(Instant.now().plus(Duration.ofDays(7)))
                            .build()
            );
        }

        ResponseCookie refreshTokenCookie = jwtUtil.createRefreshTokenCookie(refreshToken, 7);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), Map.of(
                "accessToken", accessToken,
                "memberId", member.getLoginId(),
                "message", "로그인 성공"
        ));
    }
}
