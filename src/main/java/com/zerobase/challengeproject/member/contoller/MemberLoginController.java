package com.zerobase.challengeproject.member.contoller;


import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.JwtUtil;

import com.zerobase.challengeproject.member.domain.dto.MemberLogoutDto;
import com.zerobase.challengeproject.member.domain.dto.RefreshTokenDto;
import com.zerobase.challengeproject.member.service.MemberLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberLoginController {

    private final MemberLoginService memberLoginService;
    private final JwtUtil jwtUtil;
    /**
     * 로그인한 유저가 로그 아웃을 시도할 때 사용하는 컨트롤러 메서드
     * @param token 로그인시 발핼한 AccessToken
     * @return 로그인한 유저의 아이디, 아무정보도 없는 쿠키
     */
    @PostMapping("/logout")
    public ResponseEntity<HttpApiResponse> logout(@RequestHeader("Authorization") String token) {

        MemberLogoutDto dto = memberLoginService.logout(token);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, dto.getResponseCookie().toString())
                .body(new HttpApiResponse<>(dto.getLoginId(), "로그아웃 성공했습니다.", HttpStatus.OK));
    }

    /**
     * refreshToken을 이용한 AccessToken 재발급
     * @param refreshToken 로그인시 생성된 refreshToken이 들어있는 쿠키
     * @return AccessToken
     */
    @PostMapping("/token/refresh")
    public ResponseEntity<HttpApiResponse> refreshAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_IS_INVALID_OR_EXPIRED);
        }
        RefreshTokenDto dto = memberLoginService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + dto.getAccessToken())
                .header(HttpHeaders.SET_COOKIE, dto.getRefreshToken())
                .body(new HttpApiResponse<>(null, "토큰이 재 발행되었습니다", HttpStatus.OK));
    }
}
