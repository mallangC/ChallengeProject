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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {


        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        Member member = user.getMember();


        String accessToken = jwtUtil.generateAccessToken(member.getLoginId(), member.getMemberType());
        String refreshToken = jwtUtil.generateRefreshToken(member.getLoginId(), member.getMemberType());

        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByLoginId(member.getLoginId());

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
