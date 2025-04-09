package com.zerobase.challengeproject.member.domain.dto;

import com.zerobase.challengeproject.account.domain.dto.AccountDetailDto;
import com.zerobase.challengeproject.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MemberDto {
  private String memberId;
  private String memberName;
  private String nickName;
  private String phoneNum;
  private String email;
  private Long account;
  private List<AccountDetailDto> accountDetails;

  public static MemberDto from(Member member) {
    return MemberDto.builder()
            .memberId(member.getMemberId())
            .memberName(member.getMemberName())
            .nickName(member.getNickname())
            .phoneNum(member.getPhoneNum())
            .email(member.getEmail())
            .account(member.getAccount())
            .accountDetails(member.getAccountDetails().stream()
                    .map(AccountDetailDto::from)
                    .toList())
            .build();
  }

  public static MemberDto fromWithoutAccountDetails(Member member) {
    return MemberDto.builder()
            .memberId(member.getMemberId())
            .memberName(member.getMemberName())
            .nickName(member.getNickname())
            .phoneNum(member.getPhoneNum())
            .email(member.getEmail())
            .account(member.getAccount())
            .build();
  }
}
