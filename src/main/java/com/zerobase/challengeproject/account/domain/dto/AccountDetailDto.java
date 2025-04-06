package com.zerobase.challengeproject.account.domain.dto;

import com.zerobase.challengeproject.account.entity.AccountDetail;
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
  private boolean isCharge;
  private boolean isRefunded;
  private Long preAmount;
  private Long curAmount;
  private Long amount;
  private LocalDateTime chargeDate;

  public static AccountDetailDto from(AccountDetail detail) {
    return AccountDetailDto.builder()
            .memberId(detail.getMember().getLoginId())
            .isRefunded(detail.isRefunded())
            .preAmount(detail.getPreAmount())
            .curAmount(detail.getCurAmount())
            .amount(detail.getAmount())
            .isCharge(detail.isCharge())
            .chargeDate(detail.getCreatedAt())
            .build();
  }
}
