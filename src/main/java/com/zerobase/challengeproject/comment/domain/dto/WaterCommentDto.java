package com.zerobase.challengeproject.comment.domain.dto;

import com.zerobase.challengeproject.comment.entity.WaterComment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WaterCommentDto {
  private Long id;
  private Long challengeId;
  private String longinId;
  private Integer drinkingIntake;
  private String imageUrl;

  public static WaterCommentDto from(WaterComment waterComment) {
    return WaterCommentDto.builder()
            .id(waterComment.getId())
            .challengeId(waterComment.getWaterChallenge().getChallenge().getId())
            .longinId(waterComment.getMember().getLoginId())
            .drinkingIntake(waterComment.getDrinkingIntake())
            .imageUrl(waterComment.getImageUrl())
            .build();
  }
}
