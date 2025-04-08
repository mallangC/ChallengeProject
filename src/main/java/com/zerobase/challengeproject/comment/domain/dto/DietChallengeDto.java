package com.zerobase.challengeproject.comment.domain.dto;

import com.zerobase.challengeproject.comment.entity.DietChallenge;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Builder
public class DietChallengeDto {
  private Long id;
  private String loginId;
  private Float goalWeight;
  private Float currentWeight;
  private List<DietCommentDto> comments;

  public static DietChallengeDto from(DietChallenge dietChallenge) {
    return DietChallengeDto.builder()
            .id(dietChallenge.getId())
            .loginId(dietChallenge.getMember().getMemberId())
            .goalWeight(dietChallenge.getGoalWeight())
            .currentWeight(dietChallenge.getCurrentWeight())
            .comments(dietChallenge.getComments().stream()
                    .map(DietCommentDto::from)
                    .toList())
            .build();
  }

  public static DietChallengeDto fromWithoutComments(DietChallenge dietChallenge) {
    return DietChallengeDto.builder()
            .id(dietChallenge.getId())
            .loginId(dietChallenge.getMember().getMemberId())
            .goalWeight(dietChallenge.getGoalWeight())
            .currentWeight(dietChallenge.getCurrentWeight())
            .comments(new ArrayList<>())
            .build();
  }
}
