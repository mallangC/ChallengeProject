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
  private String memberId;
  private AccountType accountType;
  private boolean isRefunded;
  private Long preAmount;
  private Long curAmount;
  private Long amount;
  private LocalDateTime chargeDate;

  public static AccountDetailDto from(AccountDetail detail) {
    return AccountDetailDto.builder()
            .memberId(detail.getMember().getMemberId())
            .isRefunded(detail.isRefunded())
            .preAmount(detail.getPreAmount())
            .curAmount(detail.getCurAmount())
            .amount(detail.getAmount())
            .accountType(detail.getAccountType())
            .chargeDate(detail.getCreatedAt())
            .build();
  }
}
