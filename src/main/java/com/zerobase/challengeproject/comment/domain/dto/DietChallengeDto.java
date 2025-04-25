package com.zerobase.challengeproject.comment.domain.dto;

import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import com.zerobase.challengeproject.comment.entity.CoteComment;
import com.zerobase.challengeproject.comment.entity.DietChallenge;
import com.zerobase.challengeproject.comment.entity.DietComment;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Getter
@Builder
public class DietChallengeDto {
  private Long id;
  private String loginId;
  private Float goalWeight;
  private Float currentWeight;
  private List<DietCommentDto> comments;

  public static DietChallengeDto from(DietChallenge dietChallenge) {
    List<DietCommentDto> commentDtos = Optional.ofNullable(dietChallenge.getComments())
            .orElse(Collections.emptyList())
            .stream()
            .map(DietCommentDto::from)
            .toList();

    return DietChallengeDto.builder()
            .id(dietChallenge.getId())
            .loginId(dietChallenge.getMember().getLoginId())
            .goalWeight(dietChallenge.getGoalWeight())
            .currentWeight(dietChallenge.getCurrentWeight())
            .comments(commentDtos)
            .build();
  }

  public static DietChallengeDto fromWithoutComments(DietChallenge dietChallenge) {
    return DietChallengeDto.builder()
            .id(dietChallenge.getId())
            .loginId(dietChallenge.getMember().getLoginId())
            .goalWeight(dietChallenge.getGoalWeight())
            .currentWeight(dietChallenge.getCurrentWeight())
            .comments(new ArrayList<>())
            .build();
  }
}
