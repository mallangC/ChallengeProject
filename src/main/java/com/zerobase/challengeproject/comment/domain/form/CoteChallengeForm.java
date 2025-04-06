package com.zerobase.challengeproject.comment.domain.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CoteChallengeForm {
  @NotNull(message = "챌린지 아이디를 입력해주세요")
  private Long challengeId;
  @NotBlank(message = "코테 문제의 제목 입력해주세요")
  private String title;
  @NotBlank(message = "코테 문제의 링크를 입력해주세요")
  private String question;
  @NotNull(message = "문제가 시작되는 날짜를 입력해주세요")
  private LocalDateTime startAt;
}
