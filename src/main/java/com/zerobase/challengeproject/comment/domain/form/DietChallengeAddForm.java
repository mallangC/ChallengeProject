package com.zerobase.challengeproject.comment.domain.form;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DietChallengeAddForm {
  @NotNull(message = "챌린지 아이디를 입력해주세요")
  private Long challengeId;
  @NotBlank(message = "인증 이미지를 등록해주세요")
  private String image;
  @Digits(integer = 3, fraction = 1, message = "소수점 1자리 까지만 입력해주세요 예)51.2")
  @NotNull(message = "목표 몸무게를 입력해주세요")
  private Float goalWeight;
  @Digits(integer = 3, fraction = 1, message = "소수점 1자리 까지만 입력해주세요 예)51.2")
  @NotNull(message = "현재 몸무게를 입력해주세요")
  private Float currentWeight;
}
