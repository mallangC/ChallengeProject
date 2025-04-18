package com.zerobase.challengeproject.account.domain.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountAddRequest {
  @Min(value = 5000, message = "5천원 이상 금액으로 충전이 가능합니다.")
  @Max(value = 100000, message = "10만원 이하 금액으로 충전이 가능합니다.")
  private Long chargeAmount;
}
