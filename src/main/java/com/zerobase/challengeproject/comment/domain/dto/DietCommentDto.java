package com.zerobase.challengeproject.comment.domain.dto;

import com.zerobase.challengeproject.comment.entity.DietComment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DietCommentDto {
  private Long id;
  private Long dietChallengeId;
  private String loginId;
  private String image;
  private String content;

  public static DietCommentDto from(DietComment dietComment) {
    return DietCommentDto.builder()
            .id(dietComment.getId())
            .dietChallengeId(dietComment.getDietChallenge().getId())
            .loginId(dietComment.getMember().getMemberId())
            .image(dietComment.getImage())
            .content(dietComment.getContent())
            .build();
  }
}
