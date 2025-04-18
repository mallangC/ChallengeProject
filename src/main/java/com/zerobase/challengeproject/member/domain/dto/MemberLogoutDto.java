package com.zerobase.challengeproject.member.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@Getter
@NoArgsConstructor
public class MemberLogoutDto {
    private String loginId;
    private ResponseCookie responseCookie;

    public MemberLogoutDto(String memberId, ResponseCookie responseCookie) {
        this.loginId = memberId;
        this.responseCookie = responseCookie;
    }
}
