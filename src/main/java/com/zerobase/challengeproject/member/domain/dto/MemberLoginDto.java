package com.zerobase.challengeproject.member.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginDto {
    private String memberId;

    public MemberLoginDto(String memberId) {
        this.memberId = memberId;
    }
}
