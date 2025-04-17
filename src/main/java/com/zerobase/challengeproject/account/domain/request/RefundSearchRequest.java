package com.zerobase.challengeproject.account.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundSearchRequest {
  private LocalDateTime startAtStr;
  private Boolean done;
  private Boolean refunded;
}
