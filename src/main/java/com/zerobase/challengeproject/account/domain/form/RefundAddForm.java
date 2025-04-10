package com.zerobase.challengeproject.account.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundAddForm {
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime startAtStr;
  private Long accountId;
  private String content;
}
