package com.zerobase.challengeproject.account.domain.form;

import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Builder
public class RefundSearchForm {
  private LocalDateTime startAtStr;
  private Boolean done;
  private Boolean refunded;
}
