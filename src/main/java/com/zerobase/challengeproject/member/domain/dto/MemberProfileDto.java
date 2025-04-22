package com.zerobase.challengeproject.member.domain.dto;

import com.zerobase.challengeproject.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberProfileDto {
    private String loginId;
    private String memberName;
    private String nickName;
    private String phoneNum;
    private String email;
    private Long account;

    public MemberProfileDto (Member member) {
        this.loginId = member.getLoginId();
        this.memberName = member.getMemberName();
        this.nickName = member.getNickname();
        this.phoneNum = member.getPhoneNumber();
        this.email = member.getEmail();
        this.account = member.getAccount();
    }
}
