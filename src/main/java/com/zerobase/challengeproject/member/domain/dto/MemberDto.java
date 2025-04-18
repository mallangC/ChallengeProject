package com.zerobase.challengeproject.member.domain.dto;

import com.zerobase.challengeproject.account.domain.dto.AccountDetailDto;
import com.zerobase.challengeproject.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MemberDto {
  private String loginId;
  private String memberName;
  private String nickname;
  private String phoneNumber;
  private String email;
  private Long account;
  private List<AccountDetailDto> accountDetails;

  public static MemberDto from(Member member) {
    return MemberDto.builder()
            .loginId(member.getLoginId())
            .memberName(member.getMemberName())
            .nickname(member.getNickname())
            .phoneNumber(member.getPhoneNumber())
            .email(member.getEmail())
            .accountDetails(member.getAccountDetails().stream()
                    .map(AccountDetailDto::from)
                    .toList())
            .account(member.getAccount())
            .build();
  }

  public static MemberDto fromWithoutAccountDetails(Member member) {
    return MemberDto.builder()
            .loginId(member.getLoginId())
            .memberName(member.getMemberName())
            .nickname(member.getNickname())
            .phoneNumber(member.getPhoneNumber())
            .email(member.getEmail())
            .account(member.getAccount())
            .build();
  }
}
