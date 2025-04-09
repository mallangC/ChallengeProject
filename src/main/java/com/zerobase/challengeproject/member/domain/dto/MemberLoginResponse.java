package com.zerobase.challengeproject.member.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@Getter
@NoArgsConstructor
public class MemberLoginResponse {
    private String accessToken;
    private ResponseCookie refreshTokenCookie;
    private String memberId;

    public MemberLoginResponse(String token,ResponseCookie refreshTokenCookie, String memberId) {
        this.accessToken = token;
        this.refreshTokenCookie = refreshTokenCookie;
        this.memberId = memberId;
    }
}