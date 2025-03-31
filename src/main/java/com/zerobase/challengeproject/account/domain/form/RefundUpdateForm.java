package com.zerobase.challengeproject.account.domain.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RefundUpdateForm {
  @NotBlank(message = "환불 신청 아이디를 입력해주세요.")
  private Long refundId;
  private String content;
}
