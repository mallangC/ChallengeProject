package com.zerobase.challengeproject.account.domain.dto;

import com.zerobase.challengeproject.account.entity.AccountDetail;
import com.zerobase.challengeproject.type.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetailDto {
  private Long id;
  private String loginId;
  private AccountType accountType;
  private boolean isRefunded;
  private Long preAmount;
  private Long curAmount;
  private Long amount;
  private LocalDateTime createdAt;

  public static AccountDetailDto from(AccountDetail detail) {
    return AccountDetailDto.builder()
            .id(detail.getId())
            .loginId(detail.getMember().getLoginId())
            .isRefunded(detail.isRefunded())
            .preAmount(detail.getPreviousAmount())
            .curAmount(detail.getCurrentAmount())
            .amount(detail.getAmount())
            .accountType(detail.getAccountType())
            .createdAt(detail.getCreatedAt())
            .build();
  }
}
