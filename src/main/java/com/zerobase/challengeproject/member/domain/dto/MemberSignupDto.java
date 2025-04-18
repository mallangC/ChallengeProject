package com.zerobase.challengeproject.member.domain.dto;

import com.zerobase.challengeproject.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class MemberSignupDto {
    private String loginId;
    private String memberName;
    private String nickName;
    private String phoneNumber;
    private String email;

    public MemberSignupDto(Member member) {
        this.loginId = member.getLoginId();
        this.memberName = member.getMemberName();
        this.nickName = member.getNickname();
        this.phoneNumber = member.getPhoneNumber();
        this.email = member.getEmail();
    }


}
