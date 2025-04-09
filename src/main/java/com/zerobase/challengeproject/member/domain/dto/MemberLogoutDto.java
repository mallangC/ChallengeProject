package com.zerobase.challengeproject.member.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@Getter
@NoArgsConstructor
public class MemberLogoutDto {
    private String memberId;
    private ResponseCookie responseCookie;

    public MemberLogoutDto(String memberId, ResponseCookie responseCookie) {
        this.memberId = memberId;
        this.responseCookie = responseCookie;
    }
}
