package com.zerobase.challengeproject.account.domain.dto;

import com.zerobase.challengeproject.account.entity.Refund;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Builder
@Getter
public class RefundDto {
  private Long id;
  private Long accountDetailId;
  private String memberContent;
  private String adminContent;
  private boolean isRefunded;
  private boolean isDone;
  private LocalDateTime createdAt;

  public static RefundDto from(Refund refund) {
    return RefundDto.builder()
            .id(refund.getId())
            .accountDetailId(refund.getAccountDetail().getId())
            .memberContent(refund.getMemberContent())
            .adminContent(refund.getAdminContent())
            .isRefunded(refund.isRefunded())
            .isDone(refund.isDone())
            .createdAt(refund.getCreatedAt())
            .build();
  }
}
