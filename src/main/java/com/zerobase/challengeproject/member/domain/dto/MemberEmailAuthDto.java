package com.zerobase.challengeproject.member.domain.dto;

import com.zerobase.challengeproject.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MemberEmailAuthDto {
    private String loginId;
    private boolean isEmailVerified;
    private LocalDateTime emailAuthDate;

    public MemberEmailAuthDto(Member member) {
        this.loginId = member.getLoginId();
        this.isEmailVerified = member.isEmailVerified();
        this.emailAuthDate = LocalDateTime.now();
    }

}
