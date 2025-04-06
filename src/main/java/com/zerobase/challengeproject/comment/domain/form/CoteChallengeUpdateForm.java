package com.zerobase.challengeproject.comment.domain.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoteChallengeUpdateForm {
  @NotNull(message = "코테 챌린지 아이디를 입력해주세요")
  private Long coteChallengeId;
  @NotBlank(message = "코테 문제의 제목 입력해주세요")
  private String title;
  @NotBlank(message = "코테 문제의 링크를 입력해주세요")
  private String question;
}
