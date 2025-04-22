package com.zerobase.challengeproject.account.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefundUpdateRequest {
  @NotBlank(message = "환불 승인 여부를 선택해주세요.")
  private Boolean approval;
  @NotBlank(message = "환불 신청 아이디를 입력해주세요.")
  private Long refundId;
  private String content;
}
