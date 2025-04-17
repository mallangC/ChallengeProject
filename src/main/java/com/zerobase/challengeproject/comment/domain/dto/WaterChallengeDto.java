package com.zerobase.challengeproject.comment.domain.dto;

import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class WaterChallengeDto {
  private Long id;
  private String loginId;
  private Long challengeId;
  private Integer goalIntake;
  private Integer currentIntake;
  private List<WaterCommentDto> comments;

  public static WaterChallengeDto from(WaterChallenge waterChallenge) {
    return WaterChallengeDto.builder()
            .id(waterChallenge.getId())
            .loginId(waterChallenge.getMember().getLoginId())
            .challengeId(waterChallenge.getId())
            .goalIntake(waterChallenge.getGoalIntake())
            .currentIntake(waterChallenge.getCurrentIntake())
            .comments(waterChallenge.getComments().stream()
                    .map(WaterCommentDto::from)
                    .toList())
            .build();
  }

  public static WaterChallengeDto fromWithoutComment(WaterChallenge waterChallenge) {
    return WaterChallengeDto.builder()
            .id(waterChallenge.getId())
            .loginId(waterChallenge.getMember().getLoginId())
            .challengeId(waterChallenge.getChallenge().getId())
            .goalIntake(waterChallenge.getGoalIntake())
            .currentIntake(waterChallenge.getCurrentIntake())
            .comments(new ArrayList<>())
            .build();
  }
}
