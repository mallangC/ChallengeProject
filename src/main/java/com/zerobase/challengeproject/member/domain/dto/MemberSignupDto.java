package com.zerobase.challengeproject.member.domain.dto;

import com.zerobase.challengeproject.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class MemberSignupDto {
    private String memberId;
    private String memberName;
    private String nickName;
    private String phoneNum;
    private String email;

    public MemberSignupDto(Member member) {
        this.memberId = member.getMemberId();
        this.memberName = member.getMemberName();
        this.nickName = member.getNickname();
        this.phoneNum = member.getPhoneNum();
        this.email = member.getEmail();
    }


}
