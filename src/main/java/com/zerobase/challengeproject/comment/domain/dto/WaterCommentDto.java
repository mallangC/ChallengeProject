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
  private Integer drinkingMl;
  private String image;

  public static WaterCommentDto from(WaterComment waterComment) {
    return WaterCommentDto.builder()
            .id(waterComment.getId())
            .challengeId(waterComment.getWaterChallenge().getChallenge().getId())
            .longinId(waterComment.getMember().getMemberId())
            .drinkingMl(waterComment.getDrinkingMl())
            .image(waterComment.getImage())
            .build();
  }
}
