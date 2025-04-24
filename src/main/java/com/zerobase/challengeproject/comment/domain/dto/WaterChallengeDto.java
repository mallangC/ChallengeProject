package com.zerobase.challengeproject.comment.domain.dto;

import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import com.zerobase.challengeproject.comment.entity.CoteComment;
import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import com.zerobase.challengeproject.comment.entity.WaterComment;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    List<WaterCommentDto> commentDtos = Optional.ofNullable(waterChallenge.getComments())
            .orElse(Collections.emptyList())
            .stream()
            .map(WaterCommentDto::from)
            .toList();

    return WaterChallengeDto.builder()
            .id(waterChallenge.getId())
            .loginId(waterChallenge.getMember().getLoginId())
            .challengeId(waterChallenge.getChallenge().getId())
            .goalIntake(waterChallenge.getGoalIntake())
            .currentIntake(waterChallenge.getCurrentIntake())
            .comments(commentDtos)
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
