package com.zerobase.challengeproject.comment.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoteCommentUpdateRequest {
  @NotNull(message = "코테 코멘트 아이디를 입력해주세요.")
  private Long commentId;
  @NotBlank(message = "수정할 이미지 주소를 입력해주세요.")
  private String imageUrl;
  @NotBlank(message = "수정할 문제풀이를 입력해주세요.")
  private String content;
}
