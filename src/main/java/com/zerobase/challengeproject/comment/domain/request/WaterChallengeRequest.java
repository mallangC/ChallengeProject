package com.zerobase.challengeproject.comment.domain.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterChallengeRequest {
 @NotNull(message = "챌린지 아이디를 입력해주세요")
 private Long challengeId;
 @NotNull(message = "하루 목표 섭취량을 입력해주세요")
 @Min(value = 1000, message = "목표 섭취량은 1000ml이상으로 설정할 수 있습니다.")
 @Max(value = 2000, message = "목표 섭취량은 2000ml이하로 설정할 수 있습니다.")
 private Integer goalIntake;
}
