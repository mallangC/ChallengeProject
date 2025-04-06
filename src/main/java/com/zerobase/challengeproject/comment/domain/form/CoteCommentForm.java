package com.zerobase.challengeproject.comment.domain.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoteCommentForm {
  @NotNull(message = "챌린지 아이디를 입력해주세요")
  private Long challengeId;
  @NotBlank(message = "이미지를 넣어주세요")
  private String image;
  @NotBlank(message = "문제풀이에 대한 설명을 입력해주세요")
  private String content;
}
