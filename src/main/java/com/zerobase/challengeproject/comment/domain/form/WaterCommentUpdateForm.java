package com.zerobase.challengeproject.comment.domain.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaterCommentUpdateForm {
  @NotNull(message = "수정할 댓글 아이디를 입력해주세요.")
  private Long commentId;
  @NotNull(message = "수정할 하루 목표 섭취량을 입력해주세요.")
  @Min(value = 100, message = "섭취량은 100ml이상으로 설정할 수 있습니다.")
  @Max(value = 1000, message = "섭취량은 1000ml이하로 설정할 수 있습니다.")
  private Integer drinkingMl;
  @NotBlank(message = "수정할 이미지를 등록해주세요.")
  private String image;
}
