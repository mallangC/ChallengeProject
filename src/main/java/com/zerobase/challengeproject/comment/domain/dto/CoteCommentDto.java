package com.zerobase.challengeproject.comment.domain.dto;

import com.zerobase.challengeproject.comment.entity.CoteComment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoteCommentDto {
  private Long id;
  private String loginId;
  private Long coteChallengeId;
  private String imageUrl;
  private String content;

  public static CoteCommentDto from(CoteComment coteComment) {
    return CoteCommentDto.builder()
            .id(coteComment.getId())
            .loginId(coteComment.getMember().getLoginId())
            .coteChallengeId(coteComment.getCoteChallenge().getId())
            .imageUrl(coteComment.getImageUrl())
            .content(coteComment.getContent())
            .build();
  }
}
