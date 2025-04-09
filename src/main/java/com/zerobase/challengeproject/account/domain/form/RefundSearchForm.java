package com.zerobase.challengeproject.account.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundSearchForm {
  private LocalDateTime startAtStr;
  private Boolean done;
  private Boolean refunded;
}
